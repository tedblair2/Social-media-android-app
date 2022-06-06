package com.example.newinstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newinstagram.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditActivity extends AppCompatActivity {
    private TextView changephoto;
    private ImageView close;
    private CircleImageView imageprofile;
    private TextView save;
    private MaterialEditText fullname;
    private MaterialEditText username;
    private MaterialEditText bio;

    private FirebaseUser fUser;
    private StorageReference storageref;
    private Uri imageUri;
    private StorageTask uploadtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        storageref= FirebaseStorage.getInstance().getReference().child("Uploads");

        changephoto=findViewById(R.id.change_photo);
        close=findViewById(R.id.close_edit);
        imageprofile=findViewById(R.id.profile_edit);
        save=findViewById(R.id.save_edit);
        fullname=findViewById(R.id.fullname_edit);
        username=findViewById(R.id.username_edit);
        bio=findViewById(R.id.bio_edit);

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        fullname.setText(user.getName());
                        username.setText(user.getUsername());
                        bio.setText(user.getBio());
                        Picasso.get().load(user.getImageurl()).into(imageprofile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditActivity.this);
            }
        });
        imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditActivity.this);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateprofile();
            }
        });

    }

    private void updateprofile() {
        HashMap<String,Object> map=new HashMap<>();
        map.put("fullname",fullname.getText().toString());
        map.put("username",username.getText().toString());
        map.put("bio",bio.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).updateChildren(map);
    }

    private void uploadimage(){
        ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        if (imageUri !=null){
            StorageReference fileref=storageref.child(System.currentTimeMillis()+".jpeg");
            uploadtask=fileref.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }else {
                        return fileref.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloaduri=task.getResult();
                        String url=downloaduri.toString();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid())
                                .child("imageurl").setValue(url);
                        pd.dismiss();
                    }else {
                        Toast.makeText(EditActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(EditActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode ==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            imageUri=result.getUri();
            uploadimage();

        }else {
            Toast.makeText(EditActivity.this, "SOmething went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}