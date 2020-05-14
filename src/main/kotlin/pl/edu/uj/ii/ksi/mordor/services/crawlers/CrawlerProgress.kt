package pl.edu.uj.ii.ksi.mordor.services.crawlers

data class CrawlerProgress(
    var total: Long = 0,
    var done: Long = 0
) {
    fun currentProgress(): Double {
        return if (active && total > 0) done.toDouble() / total.toDouble() else 1.0
    }

    var active: Boolean = false
        set(active) {
            field = active
            if (!active) {
                this.total = 0
                this.done = 0
            }
        }
}
