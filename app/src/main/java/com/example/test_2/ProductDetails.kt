package com.example.test_2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.test_2.ui.theme.Test_2Theme
import com.example.test_2.ui.theme.green
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetails : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Test_2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color(0xFF1D9372),
                                titleContentColor = Color.White,
                                navigationIconContentColor = Color.White,
                                actionIconContentColor = Color.White
                            ),
                            title = {
                                Text(
                                    text = "All Product",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { }) {
                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                                }
                            },
                            actions = {
                                IconButton(onClick = {/* Do Something*/ }) {
                                    Icon(imageVector = Icons.Filled.Settings, null)
                                }
                            }
                        )
                    }

                    var productList = mutableStateListOf<ProductData>()
                    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
                    val dbProducts: CollectionReference = db.collection("Products")

                    dbProducts.get().addOnSuccessListener { queryDocumentSnapshot ->
                        if (!queryDocumentSnapshot.isEmpty){
                            val list = queryDocumentSnapshot.documents
                            for (d in list){
                                val c: ProductData? = d.toObject(ProductData::class.java)
                                c?.productID = d.id
                                Log.e("TAG", "Course id is : " + c!!.productID)
                                productList.add(c)
                            }
                        }else{
                            Toast.makeText(
                               this@ProductDetails,
                                "No data found in Database",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@ProductDetails,
                            "Fail to get the data.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    productDetailsUI(LocalContext.current, productList)

                }
            }
        }
    }
}
private fun deleteDataFromFirebase(productID: String?, context: Context) {

    // getting our instance from Firebase Firestore.
    val db = FirebaseFirestore.getInstance();

    // below line is for getting the collection
    // where we are storing our courses.
    db.collection("Products").document(productID.toString()).delete().addOnSuccessListener {
        // displaying toast message when our course is deleted.
        Toast.makeText(context, "Product Deleted successfully..", Toast.LENGTH_SHORT).show()
        context.startActivity(Intent(context, ProductDetails::class.java))
    }.addOnFailureListener {
        // on below line displaying toast message when
        // we are not able to delete the course
        Toast.makeText(context, "Fail to delete item..", Toast.LENGTH_SHORT).show()
    }

}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun productDetailsUI(context: Context, productList: SnapshotStateList<ProductData>){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 70.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
            },
            modifier = Modifier
                .width(170.dp)
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.dp,
                pressedElevation = 6.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF36e0a2)
            ),
//                border = BorderStroke(0.5.dp, Color.Red)
        ) {
            Text(text = "Add Product", fontWeight = FontWeight.Bold,fontSize = 18.sp)
        }

        LazyColumn {
            itemsIndexed(productList) {index, item ->
                Surface(
                    color = Color(0xFFbefae0),
                    modifier = Modifier
                        .height(215.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .padding(10.dp),

                    shadowElevation = 10.dp,
                    onClick = {
                    val i = Intent(context, UpdateProducts::class.java)
                        i.putExtra("productName", item?.productName)
                        i.putExtra("productType", item?.productType)
                        i.putExtra("productPrice", item?.productPrice)
                        i.putExtra("productDescription", item?.productDescription)
                        i.putExtra("productImage", item?.productImage)
                        i.putExtra("productID", item?.productID)

                        context.startActivity(i)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.size(width = 140.dp, height = 140.dp)
                        ) {
                            productList[index]?.productImage?.let {
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(2f),
                            verticalArrangement = Arrangement.Center
                        ) {

                            Spacer(modifier = Modifier.height(6.dp))

                            productList[index]?.productName?.let {
                                Text(
                                    text = it,
                                    fontSize =  24.sp,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            productList[index]?.productType?.let {
                                Text(text = "Type: "+ it)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            productList[index]?.productPrice?.let {
                                Text(text = "Price: "+ it +" VND")
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row {
                                OutlinedButton(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = Color.Black,
                                        containerColor = Color.White
                                    ),
                                    onClick = {
                                        val i = Intent(context, UpdateProducts::class.java)
                                        i.putExtra("productName", item?.productName)
                                        i.putExtra("productType", item?.productType)
                                        i.putExtra("productPrice", item?.productPrice)
                                        i.putExtra("productDescription", item?.productDescription)
                                        i.putExtra("productImage", item?.productImage)
                                        i.putExtra("productID", item?.productID)

                                        context.startActivity(i)
                                    }
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_edit),
                                        tint= Color.Black,
                                        contentDescription = "",
                                    )
                                }
                                IconButton(
                                    onClick = { deleteDataFromFirebase(productList[index]?.productID, context) },
                                    modifier = Modifier.padding(2.dp),
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_delete),
                                        contentDescription = "",
                                    )
                                }
                            }
                        }


                    }
                }
            }
        }
    }
}
