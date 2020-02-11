package k.s.yarlykov.firebaseedu.free

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import k.s.yarlykov.firebaseedu.App
import k.s.yarlykov.firebaseedu.R
import k.s.yarlykov.firebaseedu.entities.User

/**
 * Подготовка приложения и первый запуск
 * https://firebase.google.com/docs/firestore/quickstart
 *
 * Нам нужен именно FirebaseFirestore, а не FirebaseDatabase
 *
 * FirebaseDatabase - это RealTimeDatabase
 * https://stackoverflow.com/questions/46549766/whats-the-difference-between-cloud-firestore-and-the-firebase-realtime-database
 */

private const val AUTH_USERS = "auth_users"


class MainActivity : AppCompatActivity() {

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = (application as App).db

        // Добавляем документы в коллекцию
//        addUsers()
        // Читаем всю коллекцию
//        readUsers()
        // Прослушиваем изменения в коллекции и тестируем
        addCollectionOnChangeListener()
        addIncognitoUser()
        addIncognitoUser()
    }

    /**
     * Чтобы добавить документы в несуществующую коллекцию, нужно просто указать имя
     * создаваемой коллекции в db.collection(<new_collection_name>)
     *
     * Создать документ с имененм вот так:
     *  db.collection(<collection_name>).document(<doc_name>).set(data_ref)
     *
     *  Если имя документа не имеет значения, то можно просто вот так:
     *  db.collection(<collection_name>).add(data_ref)
     *
     */
    private fun addUsers() {

        val users = listOf(
            User("Anna", 11, "anna@yakovlera"),
            User("Papa", 48, "papa@anna"),
            User("Gosha", 10, "gosha@gosha"),
            User("Masha", 17, "masha@masha")
        )

        for (user in users) {
            db.collection(NONSECURE_USERS).document(user.name).set(user)
        }

        val friend = hashMapOf<String, Any>(
            "name" to "Leha",
            "age" to 48,
            "email" to "leha@leha"
        )

        db.collection(NONSECURE_USERS).document(friend["name"] as String).set(friend)

        addIncognitoUser()
    }

    private fun addIncognitoUser() {
        val incognito = hashMapOf<String, Any>(
            "name" to "Incognito",
            "age" to 10,
            "email" to "Incognito@Incognito"
        )

        db.collection(NONSECURE_USERS).add(incognito)
    }

    /**
     * Чиатем всю коллекцию. При этом в ответ прилетает некий Snapshot
     * в качестве контейнера для документов коллекции. В цикле извлекаем
     * по одному документу и преобразовываем в инстансы User. Кстати у
     * класса User должен быть конструктор с дефолтовыми значениями.
     */
    private fun readUsers(): List<User> {
        val users = mutableListOf<User>()

        db.collection(AUTH_USERS).get()
            .addOnSuccessListener { snapshot ->

                for (document in snapshot) {
                    users.add(document.toObject(User::class.java))
                }

                users.forEach {
                    Log.d("APP_TAG", it.name)
                }
            }
            .addOnFailureListener { e ->
                Log.d("APP_TAG", e.toString())
            }

        return users
    }

    /**
     * Прослушиваем изменения в коллекции. При добавлении/удалении одного
     * документа прилетает ВСЯ коллекция !!!
     */
    private fun addCollectionOnChangeListener() {

        db
            .collection(NONSECURE_USERS)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    Log.d("APP_TAG", exception.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (document in snapshot) {
                        val user = document.toObject(User::class.java)
                        Log.d("APP_TAG", user.name)
                    }
                }
            }

    }
}
