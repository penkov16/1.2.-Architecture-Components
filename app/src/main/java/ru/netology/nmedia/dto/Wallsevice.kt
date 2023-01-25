class Wallsevice {

    fun notZeroShare(share: Long) = when (share) {
        in 0..999 -> share
        in 1_000..9_999 -> share/10_000.toDouble()
        in 10_000..99_999 -> share/10_000
        in 100_000..999_999 -> share/1000_000.toDouble()
        in 1_000_000..100_000_000 ->share/1000_000
        else ->share
    }

    fun zeroingOutShare(share: Long) = when (share) {
        in 0..999 -> share
        in 1_000..9_999 -> "${this.notZeroShare(share)} К"
        in 10_000..99_999 -> "${this.notZeroShare(share)} К"
        in 100_000..999_999 -> "${this.notZeroShare(share)} М"
        in 1_000_000..100_000_000 ->"${this.notZeroShare(share)} М"
        else ->share
    }

    fun notZeroLikes(likes: Int) = when (likes) {
        in 0..999 -> likes
        in 1_000..9_999 -> likes/10_000.toDouble()
        in 10_000..99_999 -> likes/10_000
        in 100_000..999_999 -> likes/1000_000.toDouble()
        in 1_000_000..100_000_000 ->likes/1000_000
        else ->likes
    }

    fun zeroingOutLikes(likes: Long) = when (likes) {
        in 0..999 -> likes
        in 1_000..9_999 -> "${this.notZeroShare(likes)} К"
        in 10_000..99_999 -> "${this.notZeroShare(likes)} К"
        in 100_000..999_999 -> "${this.notZeroShare(likes)} М"
        in 1_000_000..100_000_000 ->"${this.notZeroShare(likes)} М"
        else ->likes
    }
}
