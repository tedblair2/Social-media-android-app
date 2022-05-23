package com.example.newinstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private String imageurl;
    private ImageView close;
    private ImageView imageadded;
    private TextView post;
    SocialAutoCompleteTextView description;

    private Uri imageuri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close=findViewById(R.id.close);
        imageadded=findViewById(R.id.image_added);
        post=findViewById(R.id.post);
        description=findViewById(R.id.description);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimage();
            }
        });
        CropImage.activity().start(PostActivity.this);//used to select image from the gallery
    }

    private void uploadimage() {
        ProgressDialog prog=new ProgressDialog(this);
        prog.setMessage("Uploading...");
        prog.show();

        if (imageuri != null){
            final StorageReference ref= FirebaseStorage.getInstance().getReference("Posts").
                    child(System.currentTimeMillis()+"."+getfileextention(imageuri));
            StorageTask uploadtask=ref.putFile(imageuri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                    }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloaduri=task.getResult();
                    imageurl=downloaduri.toString();

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
                    String postid=reference.push().getKey();

                    HashMap<String,Object> map=new HashMap<>();
                    map.put("postid",postid);
                    map.put("imageurl",imageurl);
                    map.put("description",description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    reference.child(postid).setValue(map);

                    DatabaseReference hashtagref=FirebaseDatabase.getInstance().getReference("Hashtags");
                    List<String> list= description.getHashtags();
                    if (!list.isEmpty()){
                        for (String tag:list){
                            map.clear();
                            map.put("tag",tag.toLowerCase());
                            map.put("postid",postid);

                            hashtagref.child(tag.toLowerCase()).child(postid).setValue(map);
                        }
                    }
                    prog.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(PostActivity.this, "No image was selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getfileextention(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri=result.getUri();

            imageadded.setImageURI(imageuri);
        }else{
            Toast.makeText(PostActivity.this, "Failed!Try again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        ArrayAdapter<Hashtag> hashtagArrayAdapter=new HashtagArrayAdapter<>(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("Hashtags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot snapshot1:snapshot.getChildren()){
                   hashtagArrayAdapter.add(new Hashtag(snapshot1.getKey(),(int)snapshot1.getChildrenCount()));
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        description.setHashtagAdapter(hashtagArrayAdapter);

    }
}