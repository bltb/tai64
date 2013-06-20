# TAI64

Scala implementation of the [TAI64](http://cr.yp.to/libtai/tai64.html) time
representation. Supports TAI64, TAI64N and TAI64NA labels with conversions to
and from hexadecimal representation and [joda](joda-time.sourceforge.net)
`DateTime` objects (lossy).

## Example

```scala
import org.joda.time._
import org.joda.time.DateTimeZone.UTC
import Tai64._

val nao = DateTime.now(UTC)
val tai = nao.tai64N

tai.hex match { case Tai64N(tai2) => assume (tai2 == tai && tai2.date == nao) }
```

## Authors

* Kim Altintop <kim@soundcloud.com>
* Omid Aladini <omid@soundcloud.com>

## Contributing

[Pull requests](http://github.com/soundcloud/tai64/pulls) appreciated.

## License

Mozilla Public License 2.0, see LICENSE file
