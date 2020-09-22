package id.cikup.instagramclone.ui.add_post

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import cookode.instagram_clone.R
import kotlinx.android.synthetic.main.fragment_add_post.*


class AddPostFragment : Fragment() {

    private var myUrl = ""
    private var imageUri : Uri? = null
    private var storagePostPictureRef: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        storagePostPictureRef = FirebaseStorage.getInstance().reference.child("Post Picture")

        save_new_post_btn.setOnClickListener { uploadImage()  } //create method upload Image

        CropImage.activity()
            .setAspectRatio(2,1) //Ukurun post
            .start(this.requireActivity())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data!= null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {

        when{
            imageUri == null -> Toast.makeText(this.requireContext(),"Please select image", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(deskripsi_post.text.toString()) -> Toast.makeText(this.requireContext(),"Please write desk", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this.requireContext())
                progressDialog.setTitle("Add New Post")
                progressDialog.setMessage("Please wait, we are adding your picture...")
                progressDialog.show()

                val fileRef = storagePostPictureRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){

                        task.exception.let {
                            throw it!!
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = ref.push().key

                        val postMap = HashMap<String, Any>()
                        //sesuai dengan Firebase Database
                        postMap["postid"]      = postId!!
                        postMap["description"] = deskripsi_post.text.toString().toLowerCase()
                        postMap["publisher"]   = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"]   = myUrl

                        ref.child(postId).updateChildren(postMap)

                        Toast.makeText(this.requireContext(),"Post Success..", Toast.LENGTH_LONG).show()

//                        val intent = Intent(this@AddPostActivity, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }
}