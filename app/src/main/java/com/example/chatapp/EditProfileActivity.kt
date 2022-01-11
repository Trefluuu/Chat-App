package com.example.chatapp.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.messages.LatestMessagesActivity
import com.example.chatapp.models.ChatModel
import com.example.chatapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.IOException
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    companion object {
        var currentUser: UserModel? = null
        private val RequestCode = 438
        private val PICK_IMAGE_REQUEST = 71
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        fetchCurrentUser()

        btnUpdate_edit_profile.setOnClickListener {
            updateData()
        }
        btnSelectPhoto_editProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }
    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            selectedPhotoUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
                selectPhoto_imageView_editProfile.setImageBitmap(bitmap)
                btnSelectPhoto_editProfile.alpha = 0f
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance("https://chatapp-a0cb5-default-rtdb.europe-west1.firebasedatabase.app").getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(UserModel::class.java)
                val username = currentUser?.username
                var imageDisplay = selectPhoto_imageView_editProfile
                Picasso.get().load(currentUser?.profileImageUrl).into(imageDisplay)
                username_editText_editProfile.setText(username)
                btnSelectPhoto_editProfile.alpha = 0f
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun updateData() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    val uid = FirebaseAuth.getInstance().uid
                    val reference = FirebaseDatabase.getInstance("https://chatapp-a0cb5-default-rtdb.europe-west1.firebasedatabase.app").getReference("/users")
                    val user = mapOf<String, String>(
                        "username" to  username_editText_editProfile.text.toString(),
                        "profileImageUrl" to it.toString()
                    )
                    if (uid != null) {
                        reference.child(uid).updateChildren(user).addOnSuccessListener {
                            Log.d("EditProfile", "image: $selectedPhotoUri")
                            Toast.makeText(this, "Update successfully", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }

}

