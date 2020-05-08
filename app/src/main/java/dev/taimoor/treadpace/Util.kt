package dev.taimoor.treadpace


class Util {

    companion object {
        @JvmStatic
        // https://www.reddit.com/r/androiddev/comments/6nuxb8/null_checking_multiple_vars_in_kotlin/ reddit user gonemad16
        // take in 2 variables, p1 and p2, and a lambda
        // if both vars aren't none, execute the block
        // TODO: Generalize this with any number of arguments?
        fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
            return if (p1 != null && p2 != null) block(p1, p2) else null
        }


        @JvmStatic
        val myTag = "FromTaimoor"

        @JvmStatic
        val save_run = 1
        @JvmStatic
        val delete_run = 2

    }

}
