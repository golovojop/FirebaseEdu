package k.s.yarlykov.firebaseedu.auth

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import k.s.yarlykov.firebaseedu.App
import k.s.yarlykov.firebaseedu.R
import k.s.yarlykov.firebaseedu.entities.User
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Подготовка приложения и первый запуск
 * https://firebase.google.com/docs/firestore/quickstart
 *
 * Нам нужен именно FirebaseFirestore, а не FirebaseDatabase
 *
 * FirebaseDatabase - это RealTimeDatabase
 * https://stackoverflow.com/questions/46549766/whats-the-difference-between-cloud-firestore-and-the-firebase-realtime-database
 */

private const val AUTH_USERS = "users_auth"

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // DI
        ((application as App)).also { app ->
            db = app.db
            auth = app.auth
        }

        button_sign_up.setOnClickListener {
            signUp()
        }

        button_sign_in.setOnClickListener {
            signIn()
        }

        // Добавляем документы в коллекцию
//        addUsers()
        // Читаем всю коллекцию
//        readUsers()
        // Прослушиваем изменения в коллекции и тестируем
        addCollectionOnChangeListener()
//        addIncognitoUser()
//        addIncognitoUser()
    }

    override fun onStart() {
        super.onStart()

        auth.currentUser?.let {
            showSnackBar("Already authenticated")
        }
    }

    private fun signIn() {
//        val email = et_email.text.toString()
//        val password = et_password.text.toString()

        // Короче, нужно либо вручную создать юзера через Firebase-консоль аппа,
        // Либо воспользваться методом signUp(). См. ниже
        val email = "ciscoff@mail.ru"
        val password = "123123"

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    showSnackBar("Authenticated successfully")
                    addIncognitoUser()
                    addIncognitoUser()

                } else {
                    showSnackBar("Sign In Failed", true)
                }
            }
    }

    private fun signUp() {
//        val email = et_email.text.toString()
//        val password = et_password.text.toString()

        val email = "ciscoff@ya.ru"
        val password = "123123"

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    showSnackBar("Signed Up successfully")
                } else {
                    showSnackBar("Sign Un Failed", true)
                }
            }

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
            User("Anna_auth", 11, "anna@yakovleva"),
            User("Papa_auth", 48, "papa@anna"),
            User("Gosha_auth", 10, "gosha@gosha"),
            User("Masha_auth", 17, "masha@masha")
        )

        for (user in users) {
            db.collection(AUTH_USERS).document(user.name).set(user)
        }

        val friend = hashMapOf<String, Any>(
            "name" to "Leha_auth",
            "age" to 48,
            "email" to "leha@leha"
        )

        db.collection(AUTH_USERS).document(friend["name"] as String).set(friend)

        addIncognitoUser()
    }

    private fun addIncognitoUser() {
        val incognito = hashMapOf<String, Any>(
            "name" to "Incognito_auth",
            "age" to 10,
            "email" to "Incognito@Incognito"
        )

        db.collection(AUTH_USERS).add(incognito)
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
            .collection(AUTH_USERS)
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

    // Показать SnackBar
    private fun showSnackBar(message: String, isAlert: Boolean = false) {

        val bgColorId = if (isAlert) android.R.color.holo_red_dark else R.color.colorPrimary

        Snackbar
            .make(findViewById(R.id.cl_snackbar_container), message, Snackbar.LENGTH_SHORT)
            .setTextColor(Color.WHITE)
            .setBackgroundTint(ContextCompat.getColor(this, bgColorId))
            .show()
    }
}
