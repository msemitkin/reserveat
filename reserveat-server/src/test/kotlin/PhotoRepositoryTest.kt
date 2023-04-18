import com.reserveat.repository.PhotoRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.testng.AssertJUnit.assertEquals
import java.net.URL
import javax.management.Query.eq

@RunWith(MockitoJUnitRunner::class)
class PhotoRepositoryTest {

    @Mock
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    private lateinit var photoRepository: PhotoRepository

    @Before
    fun setUp() {
        photoRepository = PhotoRepository(jdbcTemplate)
    }

    @Test
    fun testAddLocationPhoto() {
        // Arrange
        val uuid = "123"
        val locationId = 456
        val url = URL("http://example.com/image.jpg")

        // Act
        val photo = photoRepository.addLocationPhoto(uuid, locationId, url)

        // Assert
        assertEquals(uuid, photo.id)
        assertEquals(url.toString(), photo.url)

        verify(jdbcTemplate).update(anyString(), eq(mapOf(
            "id" to uuid,
            "location_id" to locationId,
            "photo_url" to url.toString()
        )))
    }

    @Test
    fun testGetLocationPhotos() {
        // Arrange
        val locationId = 123
        val photoRowMapper = PhotoRepository.PHOTO_ROW_MAPPER
        val photos = listOf(
            Photo("1", "http://example.com/image1.jpg"),
            Photo("2", "http://example.com/image2.jpg")
        )

        `when`(jdbcTemplate.queryForStream(anyString(), anyMap(), eq(photoRowMapper)))
            .thenReturn(photos.stream())

        // Act
        val result = photoRepository.getLocationPhotos(locationId)

        // Assert
        assertEquals(photos, result)
        verify(jdbcTemplate).queryForStream(
            eq("SELECT * FROM location_photo WHERE location_id = :location_id"),
            eq(mapOf("location_id" to locationId)),
            eq(photoRowMapper)
        )
    }

    @Test
    fun testDelete() {
        // Arrange
        val id = "123"

        // Act
        photoRepository.delete(id)

        // Assert
        verify(jdbcTemplate).update(
            eq("DELETE FROM location_photo WHERE id = :id"),
            eq(mapOf("id" to id))
        )
    }
}
