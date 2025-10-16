package com.example.crudfirebasescloudstoreandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crudfirebasescloudstoreandroid.ui.theme.CrudfirebasesCloudStoreAndroidTheme
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import androidx.activity.compose.setContent
import androidx.compose.material3.CardDefaults


class CourseDetailsActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrudfirebasesCloudStoreAndroidTheme {
                // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {

                            var courseList = mutableStateListOf<Course?>()
                            // on below line creating variable for freebase database
                            // and database reference.
                            var db: FirebaseFirestore =
                                FirebaseFirestore.getInstance()

                            // on below line getting data from our database
                            db.collection("Courses").get()
                                .addOnSuccessListener { queryDocumentSnapshots ->
                                    // after getting the data we are calling
                                    // on success method
                                    // and inside this method we are checking
                                    // if the received query snapshot is empty or not.
                                    if (!queryDocumentSnapshots.isEmpty) {
                                        // if the snapshot is not empty we are
                                        // hiding our progress bar and adding
                                        // our data in a list.
                                        // loadingPB.setVisibility(View.GONE)
                                        val list = queryDocumentSnapshots.documents
                                        for (d in list) {
                                            // after getting this list we are passing that
                                            // list to our object class.
                                            val c: Course? =
                                                d.toObject(Course::class.java)
                                            // and we will pass this object class inside
                                            // our arraylist which we have created for list view.
                                            courseList.add(c)

                                        }
                                    } else {
                                        // if the snapshot is empty we are displaying
                                        // a toast message.
                                        Toast.makeText(
                                            this@CourseDetailsActivity,
                                            "No data found in Database",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                // if we don't get any data or any error
                                // we are displaying a toast message
                                // that we donot get any data
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@CourseDetailsActivity,
                                        "Fail to get the data.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            // on below line we are calling method to display UI
                            firebaseUI(LocalContext.current, courseList)




                            // ket thuc

                        }
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun firebaseUI(context: Context, courseList: SnapshotStateList<Course?>) {

    // on below line creating a column
    // to display our retrieved list.
    Column(
        // adding modifier for our column
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White),
        // on below line adding vertical and
        // horizontal alignment for column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // on below line we are
        // calling lazy column
        // for displaying listview.
        LazyColumn {
            // on below line we are setting data
            // for each item of our listview.
            itemsIndexed(courseList) { index, item ->
                // on below line we are creating
                // a card for our list view item.

                Card(onClick = {
                    // inside on click we are
                    // displaying the toast message.
                    Toast.makeText(
                        context,
                        courseList[index]?.courseName + " selected..",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                    // on below line we are adding
                    // padding from our all sides.
                    modifier = Modifier.padding(8.dp),

                    // on below line we are adding
                    // elevation for the card.
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    // on below line we are creating
                    // a row for our list view item.
                    Column(
                        // for our row we are adding modifier
                        // to set padding from all sides.
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        // on below line inside row we are adding spacer
                        Spacer(modifier = Modifier.width(5.dp))
                        // on below line we are displaying course name.
                        courseList[index]?.courseName?.let {
                            Text(
                                // inside the text on below line we are
                                // setting text as the language name
                                // from our modal class.
                                text = it,

                                // on below line we are adding padding
                                // for our text from all sides.
                                modifier = Modifier.padding(4.dp),

                                // on below line we are adding
                                // color for our text
                                color = Color.Green,
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontSize = 20.sp, fontWeight =
                                        FontWeight.Bold
                                )
                            )
                        }
                        // adding spacer on below line.
                        Spacer(modifier = Modifier.height(5.dp))

                        // on below line displaying text for course duration
                        courseList[index]?.courseDuration?.let {
                            Text(
                                // inside the text on below line we are
                                // setting text as the language name
                                // from our modal class.
                                text = it,

                                // on below line we are adding padding
                                // for our text from all sides.
                                modifier = Modifier.padding(4.dp),

                                // on below line we are
                                // adding color for our text
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontSize = 15.sp
                                )
                            )
                        }
                        // adding spacer on below line.
                        Spacer(modifier = Modifier.width(5.dp))

                        // on below line displaying text for course description
                        courseList[index]?.courseDescription?.let {
                            Text(
                                // inside the text on below line we are
                                // setting text as the language name
                                // from our modal class.
                                text = it,

                                // on below line we are adding padding
                                // for our text from all sides.
                                modifier = Modifier.padding(4.dp),

                                // on below line we are adding color for our text
                                        color = Color.Black,
                                textAlign = TextAlign.Center,
                                style = TextStyle(fontSize = 15.sp)
                            )
                        }
                    }
                }
            }

        }
    }
}



fun addDataToFirebase(
    courseName: String, courseDuration: String, courseDescription: String,
    context: Context
) {
    // on below line creating an instance of firebase firestore.
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // creating a collection reference for our Firebase Firestore database.
    val dbCourses: CollectionReference = db.collection("Courses")
// adding our data to our courses object class.
    val courses = Course(courseName, courseDescription, courseDuration)
// below method is use to add data to Firebase Firestore
// after the data addition is successful
    dbCourses.add(courses).addOnSuccessListener {
// we are displaying a success toast message.
        Toast.makeText(
            context, "Your Course has been added to Firebase Firestore",
            Toast.LENGTH_SHORT
        ).show()
    }.addOnFailureListener { e ->
// this method is called when the data addition process is failed.
// displaying a toast message when data addition is failed.
        Toast.makeText(context, "Fail to add course \n$e",
            Toast.LENGTH_SHORT).show()
    }
}