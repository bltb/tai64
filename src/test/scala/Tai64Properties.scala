import org.scalacheck._
import org.scalacheck.Prop._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._

object Tai64Properties extends Properties("Tai64") {

    import Tai64._


    val genTai64   = for(secs <- posNum[Long]) yield Tai64(secs)
    val genTai64N  = for(secs <- posNum[Long]; nanos <- posNum[Long])
                         yield Tai64N(secs, nanos)
    val genTai64NA = for {
                         secs  <- posNum[Long]
                         nanos <- posNum[Long]
                         attos <- posNum[Long]
                     } yield Tai64NA(secs, nanos, attos)

    property("Tai64: fromHex ̊̊∘ toHex ≡ id") =
        forAll(genTai64) { (a: Tai64) => a.hex match {
            case Tai64(b) => b == a
        }}

    property("Tai64N: fromHex ∘ toHex ≡ id") =
        forAll(genTai64N) { (a: Tai64N) => a.hex match {
            case Tai64N(b) => b == a
        }}

    property("Tai64NA: fromHex ̊̊∘ toHex ≡ id") =
        forAll(genTai64NA) { (a: Tai64NA) => a.hex match {
            case Tai64NA(b) => b == a
        }}


    property("Tai64: toTai ∘ toDate ≡ id") =
        forAll(genTai64) { (a: Tai64) => a.date.tai64 == a }

    property("Tai64N: toTai ∘ toDate ≡ id (loosing precision)") =
        forAll(genTai64N) { (a: Tai64N) => a.date.tai64N == a.copy(nanos = 0) }

    property("Tai64NA: toTai ∘ toDate ≡ id (loosing precision)") =
        forAll(genTai64NA) { (a: Tai64NA) =>
            a.date.tai64NA == a.copy(nanos = 0, attos = 0)
        }


    property("Tai64: fromHex ∘ toHex ∘ toTai ∘ toDate ≡ id") =
        forAll(genTai64) { (a: Tai64) => a.date.tai64.hex match {
            case Tai64(b) => b == a
        }}

    property("Tai64N: fromHex ∘ toHex ∘ toTai ∘ toDate ≡ id (loosing precision)") =
        forAll(genTai64N) { (a: Tai64N) => a.date.tai64N.hex match {
            case Tai64N(b) => b == a.copy(nanos = 0)
        }}

    property("Tai64NA: fromHex ∘ toHex ∘ toTai ∘ toDate ≡ id (loosing precision)") =
        forAll(genTai64NA) { (a: Tai64NA) => a.date.tai64NA.hex match {
            case Tai64NA(b) => b == a.copy(nanos = 0, attos = 0)
        }}
}

// vim: set ts=4 sw=4 et:
