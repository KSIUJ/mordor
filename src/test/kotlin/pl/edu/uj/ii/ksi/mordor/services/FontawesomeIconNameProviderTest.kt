package pl.edu.uj.ii.ksi.mordor.services

import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryFile

class FontawesomeIconNameProviderTest {
    companion object {
        private val testDirectory: RepositoryDirectory = mock()
        private val testPdf = RepositoryFile("test.pdf", "home/test.pdf",
            mock(), null, null, null, "application/pdf", null)
        private val testJava = RepositoryFile("test.java", "home/test.java",
            mock(), null, null, null, "test/plain", null)
        private val testImage = RepositoryFile("test", "home/test.jpg",
            mock(), null, null, null, "image/jpg", null)
        private val testUnknown = RepositoryFile("test", "home/test",
            mock(), null, null, null, "application/octet-stream", null)
    }

    private val iconNameProvider: IconNameProvider = FontawesomeIconNameProvider()

    @Test
    fun getIconName_directory() {
        assertThat(iconNameProvider.getIconName(testDirectory), equalTo("fa-folder"))
    }

    @Test
    fun getIconName_pdf() {
        assertThat(iconNameProvider.getIconName(testPdf), equalTo("fa-file-pdf"))
    }

    @Test
    fun getIconName_code() {
        assertThat(iconNameProvider.getIconName(testJava), equalTo("fa-file-code"))
    }

    @Test
    fun getIconName_image() {
        assertThat(iconNameProvider.getIconName(testImage), equalTo("fa-file-image"))
    }

    @Test
    fun getIconName_unknown() {
        assertThat(iconNameProvider.getIconName(testUnknown), equalTo("fa-file"))
    }
}
