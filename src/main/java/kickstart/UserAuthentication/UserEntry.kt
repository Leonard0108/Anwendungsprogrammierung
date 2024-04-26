package kickstart.UserAuthentication




import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany


@Entity
data class UserEntry(
    @Id @GeneratedValue private var id: Long? = null,
    var name: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var password: String? = null,

)
