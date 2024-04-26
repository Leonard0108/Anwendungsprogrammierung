package kickstart.UserAuthentication




import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id




@Entity
data class UserEntry(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long?      = null,
    var name: String?          = null,
    var lastName: String?      = null,
    var email: String?         = null,
    var password: String?      = null,
    var streetAddress: String? = null,
    var streetNumber: String?  = null,
    var city: String?          = null,
    var state: String?         = null,
    var country: String?       = null,
)
