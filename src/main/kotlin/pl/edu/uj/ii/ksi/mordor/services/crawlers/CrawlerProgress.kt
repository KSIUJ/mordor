package pl.edu.uj.ii.ksi.mordor.services.crawlers

class CrawlerProgress(
    private var active: Boolean = false,
    var total: Long = 0,
    var done: Long = 0
) {
    fun currentProgress(): Double {
        return if (total > 0) done.toDouble() / total.toDouble() else 1.0
    }

    fun active(active: Boolean) {
        this.active = active
        if (!active) {
            this.total = 0
            this.done = 0
        }
    }

    fun active(): Boolean {
        return this.active
    }
}
