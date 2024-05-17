package com.example.test_2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.test_2.ui.theme.Test_2Theme
import com.example.test_2.ui.theme.grayFont
import com.example.test_2.ui.theme.green
import com.example.test_2.ui.theme.pinkitemshadow
import com.example.test_2.ui.theme.yellow1
import com.example.test_2.ui.theme.yellow2
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProducts : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
                                containerColor = green,
                                titleContentColor = Color.White,
                                navigationIconContentColor = Color.White,
                                actionIconContentColor = Color.White
                            ),
                            title = {
                                Text(
                                    text = "Edit Product Item",
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

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp, 50.dp, 0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){

                        updateProductUI(
                            LocalContext.current,
                            intent.getStringExtra("productName"),
                            intent.getStringExtra("productType"),
                            intent.getStringExtra("productPrice"),
                            intent.getStringExtra("productDescription"),
                            intent.getStringExtra("productImage"),
                            intent.getStringExtra("productID"),
                        )

                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun updateProductUI(
    context: Context,
    name: String?,
    type: String?,
    price: String?,
    description: String?,
    imgurl: String?,
    productID: String?
){
    val productName = remember {
        mutableStateOf(name)
    }
    val productType = remember {
        mutableStateOf(type)
    }
    val productPrice = remember {
        mutableStateOf(price)
    }
    val productDescription = remember {
        mutableStateOf(description)
    }
    val productImage = remember {
        mutableStateOf(imgurl)
    }
    val newImageUrl = remember {
        mutableStateOf<String?>(null)
    }
    val scrollState = rememberScrollState()
    val categories = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    // Fetch categories from Firestore
    LaunchedEffect(Unit) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val dbCategory: CollectionReference = db.collection("Category")

        dbCategory.get().addOnSuccessListener { querySnapshot ->
            val list = querySnapshot.documents.mapNotNull {
                Pair(it.id, it.getString("categoryName") ?: "")
            }
            categories.value = list
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting categories", exception)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = productName.value.toString(),
            onValueChange = { productName.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = green,
                unfocusedBorderColor = Color.LightGray
            ),
            label = { Text(text = "Name", color = grayFont)}
        )
        Spacer(modifier = Modifier.size(7.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Category",
                color = grayFont
            )
            Spacer(modifier = Modifier.size(7.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(width = 1.dp, color = Color.LightGray),
                modifier = Modifier.height(58.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = productType.value.toString(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp),
                        style = TextStyle(color = Color.Black),
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_drop),
                            contentDescription = "Dropdown Icon",
                            modifier = Modifier.padding(end=15.dp)
                        )
                    }
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(150.dp)
            ) {
                categories.value.forEach { (categoryId,categoryName) ->
                    DropdownMenuItem(
                        {
                            Text(text = categoryName, color = Color.Black)
                        },
                        onClick = {
                            productType.value = categoryName
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(7.dp))
        OutlinedTextField(
            value = productPrice.value.toString(),
            onValueChange = { productPrice.value = it},
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = green,
                unfocusedBorderColor = Color.LightGray
            ),
            label = { Text(text = "Price", color = grayFont)}
        )
        Spacer(modifier = Modifier.size(7.dp))
        OutlinedTextField(
            value = productDescription.value.toString(),
            onValueChange = { productDescription.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = green,
                unfocusedBorderColor = Color.LightGray
            ),
            label = { Text(text = "Description", color = grayFont)}
        )
        Spacer(modifier = Modifier.size(7.dp))
        SelectItemImageSection { imageUrl ->
            // Gán URL mới cho mutable state variable
            newImageUrl.value = imageUrl
        }
        Spacer(modifier = Modifier.size(30.dp))
        Button(
            onClick = {
                if (TextUtils.isEmpty(productName.value.toString())) {
                    Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT)
                        .show()
                } else if (TextUtils.isEmpty(productPrice.value.toString())) {
                    Toast.makeText(
                        context,
                        "Please enter Price",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (TextUtils.isEmpty(productDescription.value.toString())) {
                    Toast.makeText(
                        context,
                        "Please enter description",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val imageUrl = newImageUrl.value ?: productImage.value
                    updateDataToFirebase(
                        productID,
                        productName.value,
                        productType.value,
                        productPrice.value,
                        productDescription.value,
                        imageUrl,
                        context
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.dp,
                pressedElevation = 6.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = green
            ),
//                border = BorderStroke(0.5.dp, Color.Red)
        ) {
            Text(text = "UPDATE", fontWeight = FontWeight.Bold,fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.size(30.dp))
        TextButton(onClick = {
            context.startActivity(Intent(context, ProductDetails::class.java))
        },
        ) {
            Text(
                text = "Back",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
            )
        }
    }

}

private fun updateDataToFirebase(
    productID: String?,
    name: String?,
    type: String?,
    price: String?,
    description: String?,
    imgurl: String?,
    context: Context
) {
    val updatedProduct = ProductData(productID, name, type, price, description, imgurl)

    // getting our instance from Firebase Firestore.
    val db = FirebaseFirestore.getInstance();
    db.collection("Products").document(productID.toString()).set(updatedProduct)
        .addOnSuccessListener {
            // on below line displaying toast message and opening
            // new activity to view courses.
            Toast.makeText(context, "Product Updated successfully..", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, ProductDetails::class.java))
            //  finish()

        }.addOnFailureListener {
            Toast.makeText(context, "Fail to update product : " + it.message, Toast.LENGTH_SHORT)
                .show()
        }
}
