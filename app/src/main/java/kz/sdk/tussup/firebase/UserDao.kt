package kz.sdk.tussup.firebase

import com.google.firebase.auth.FirebaseAuth

class UserDao(
    private var firebaseAuth: FirebaseAuth
) : FRDBWrapper<User>() {


    override fun getTableName(): String {
        return firebaseAuth?.currentUser?.uid.toString()
    }

    override fun getClassType(): Class<User> {
        return User::class.java
    }
}