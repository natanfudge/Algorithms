package linearAlg

interface Field<T> {
    operator fun T.plus(other: T): T
    operator fun T.unaryMinus(): T
    operator fun T.times(other: T): T

    fun T.inverse(): T

    val Zero: T
    val One: T
}

sealed interface NumericField<T> : Field<T> {
    operator fun T.times(num: Int): T
}

context (Field<T>)
fun <T> T.pow(power: Int): T {
    require(power >= 0)
    var value = One
    repeat(power){
        value *= this
    }
    return value
}


context (Field<T>)
        operator fun <T> T.minus(other: T): T {
    return this + -other
}

context (Field<T>)
        operator fun <T> T.div(other: T): T {
    return this * other.inverse()
}

object RealNumbers : NumericField<Number> {
    override fun Number.plus(other: Number): Number {
        return when (this) {
            is Int -> {
                require(other is Int)
                this + other
            }

            else -> this.toDouble() + other.toDouble()
        }
    }

    override fun Number.times(other: Number): Number {
        return when (this) {
            is Int -> {
                if (other is Int) this * other
                else this * other.toDouble()
            }

            else -> (this.toDouble() * other.toDouble())
        }
    }


    override fun Number.unaryMinus(): Number {
        return when (this) {
            is Int -> -this
            else -> -this.toDouble()
        }
    }

    override fun Number.inverse(): Number {
        return 1.0 / this.toDouble()
    }

    override fun Number.times(num: Int): Number {
        return this * (num as Number)
    }

    override val Zero: Number = 0
    override val One: Number = 1
}

object ComplexNumbers : NumericField<Complex> {
    override fun Complex.plus(other: Complex): Complex  = this + other

    override fun Complex.unaryMinus(): Complex  = -this

    override fun Complex.times(other: Complex): Complex  = this * other

    override fun Complex.inverse(): Complex  = inverse
    override fun Complex.times(num: Int): Complex  = this * Complex(num, 0)

    override val Zero: Complex = Complex.Zero
    override val One: Complex = Complex.One

}

context (Field<L>)
fun <T, L> List<T>.sumOf(selector: (T) -> L): L {
    require(isNotEmpty())
    var sum = selector(this[0])
    for (item in this.drop(1)) {
        sum += selector(item)
    }
    return sum
}

context (Field<T>)
fun <T> List<T>.sum(): T {
    return sumOf { it }
}

context (Field<T>)
fun <T> T.negativeOnOddIndex(index: Int) = if (index % 2 == 0) this else -this

context (Field<T>)
fun <T> T.negativeOnEvenIndex(index: Int) = if (index % 2 == 0) -this else this