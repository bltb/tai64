object Tai64 {

    import scala.util.control.Exception._
    import java.lang.Long.{ parseLong, toHexString }
    import org.joda.time.{ DateTime, Duration }
    import org.joda.time.DateTimeZone.UTC


    final val BaseDate = new DateTime(1970, 1, 1, 0, 0, 0, 0, UTC);
    final val TaiBase  = 4611686018427387904L


    final case class Tai64(secs: Long)
    final case class Tai64N(secs: Long, nanos: Long)
    final case class Tai64NA(secs: Long, nanos: Long, attos: Long)

    object Tai64   { def unapply(hex: String) = implicitly[Ext[Tai64]]   fromHex hex }
    object Tai64N  { def unapply(hex: String) = implicitly[Ext[Tai64N]]  fromHex hex }
    object Tai64NA { def unapply(hex: String) = implicitly[Ext[Tai64NA]] fromHex hex }

    // "external representation" (as hex)

    trait Ext[A] {
        def toHex(a: A): String
        def fromHex(s: String): Option[A]
    }

    implicit class InfixExt[A : Ext](a: A) {
        lazy val hex: String = implicitly[Ext[A]].toHex(a)
    }

    implicit lazy
    val E64: Ext[Tai64] = new Ext[Tai64] {
        def toHex(tai: Tai64) =
            toHexString(tai.secs + TaiBase).reverse.padTo(16, '0').reverse

        def fromHex(hex: String) =
            allCatch.opt { Tai64(parseLong(hex.take(16), 16) - TaiBase) }
    }

    implicit lazy
    val E64N: Ext[Tai64N] = new Ext[Tai64N] {
        def toHex(tai: Tai64N) =
            Tai64(tai.secs).hex +
            toHexString(tai.nanos).reverse.padTo(8, '0').reverse

        def fromHex(hex: String) = allCatch.opt {
            hex.splitAt(16) match { case (Tai64(tai), nanos) =>
                Tai64N(tai.secs, parseLong(nanos.take(8), 16))
            }
        }
    }

    implicit lazy
    val E64NA: Ext[Tai64NA] = new Ext[Tai64NA] {
        def toHex(tai: Tai64NA) =
            Tai64N(tai.secs, tai.nanos).hex +
            toHexString(tai.attos).reverse.padTo(8, '0').reverse

        def fromHex(hex: String) = allCatch.opt {
            hex.splitAt(24) match { case (Tai64N(tain), attos) =>
                Tai64NA(tain.secs, tain.nanos, parseLong(attos.take(8), 16))
            }
        }
    }

    // date representation -- lossy!

    implicit class DateToTai(d: DateTime) {
        def tai64: Tai64 = Tai64(tai64N.secs)

        def tai64N: Tai64N = {
            val delta = new Duration(BaseDate, d.plusSeconds(leapSecond(d)))
            Tai64N( delta.getStandardSeconds
                  , (delta.getMillis - delta.getStandardSeconds * 1000) * 1000000
                  )
        }

        def tai64NA: Tai64NA = tai64N match {
            case Tai64N(secs, nanos) => Tai64NA(secs, nanos, 0)
        }
    }

    implicit class Tai64ToDate(tai: Tai64) {
        lazy val date: DateTime = Tai64N(tai.secs, 0).date
    }
    implicit class Tai64NToDate(tai: Tai64N) {
        lazy val date: DateTime = {
            val d = BaseDate.plus(new Duration(tai.secs * 1000 + tai.nanos / 1000000))
            d.minusSeconds(leapSecond(d))
        }
    }
    implicit class Tai64NAToDate(tai: Tai64NA) {
        lazy val date: DateTime = Tai64N(tai.secs, tai.nanos).date
    }

    def leapSecond(t: DateTime): Int =
        LeapSeconds.reverse.find(_._1.isBefore(t)).map(_._2).getOrElse(0)

    final val LeapSeconds: List[(DateTime, Int)] =
        List( new DateTime(1972, 1,  1, 0 , 0) -> 10
            , new DateTime(1972, 7,  1, 0 , 0) -> 11
            , new DateTime(1973, 1,  1, 0 , 0) -> 12
            , new DateTime(1974, 1,  1, 0 , 0) -> 13
            , new DateTime(1975, 1,  1, 0 , 0) -> 14
            , new DateTime(1976, 1,  1, 0 , 0) -> 15
            , new DateTime(1977, 1,  1, 0 , 0) -> 16
            , new DateTime(1978, 1,  1, 0 , 0) -> 17
            , new DateTime(1979, 1,  1, 0 , 0) -> 18
            , new DateTime(1980, 1,  1, 0 , 0) -> 19
            , new DateTime(1981, 7,  1, 0 , 0) -> 20
            , new DateTime(1982, 7,  1, 0 , 0) -> 21
            , new DateTime(1983, 7,  1, 0 , 0) -> 22
            , new DateTime(1985, 7,  1, 0 , 0) -> 23
            , new DateTime(1988, 1,  1, 0 , 0) -> 24
            , new DateTime(1990, 1,  1, 0 , 0) -> 25
            , new DateTime(1991, 1,  1, 0 , 0) -> 26
            , new DateTime(1992, 7,  1, 0 , 0) -> 27
            , new DateTime(1993, 7,  1, 0 , 0) -> 28
            , new DateTime(1994, 7,  1, 0 , 0) -> 29
            , new DateTime(1996, 1,  1, 0 , 0) -> 30
            , new DateTime(1997, 7,  1, 0 , 0) -> 31
            , new DateTime(1999, 1,  1, 0 , 0) -> 32
            , new DateTime(2006, 1,  1, 0 , 0) -> 33
            , new DateTime(2009, 1,  1, 0 , 0) -> 34
            , new DateTime(2012, 7,  1, 0 , 0) -> 35
            )
}


// vim: set ts=4 sw=4 et:
