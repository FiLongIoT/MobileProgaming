package com.example.banhang

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import java.util.UUID

// Data class Product
data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val type: String = "",
    val price: String = "",
    val imageUrl: String = ""
)

// New Color Palette - Vibrant & Modern
val DeepPurple = Color(0xFF6B4CE6)
val LightPurple = Color(0xFF9D7FEA)
val SoftPink = Color(0xFFFCE4EC)
val MintGreen = Color(0xFFE8F5E9)
val DarkText = Color(0xFF2D3748)
val LightGray = Color(0xFFF7FAFC)
val AccentOrange = Color(0xFFFF6B35)
val AccentGreen = Color(0xFF4CAF50)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try { FirebaseApp.initializeApp(this) }
        catch (e: Exception) { Log.e("Firebase", "Firebase init error:", e) }

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = DeepPurple,
                    onPrimary = Color.White,
                    background = LightGray
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ProductManagementScreen()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen() {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }
    val products = remember { mutableStateListOf<Product>() }

    var productName by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productImageUrl by remember { mutableStateOf("") }

    var isEditing by remember { mutableStateOf(false) }
    var currentProductEdit by remember { mutableStateOf<Product?>(null) }

    val buttonText = if (isEditing) "Cập Nhật" else "Thêm Mới"

    DisposableEffect(Unit) {
        val listener: ListenerRegistration = db.collection("products")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    products.clear()
                    for (doc in snapshot.documents) {
                        val product = doc.toObject<Product>()?.copy(id = doc.id)
                        if (product != null) products.add(product)
                    }
                }
            }
        onDispose { listener.remove() }
    }

    fun reset() {
        productName = ""
        productType = ""
        productPrice = ""
        productImageUrl = ""
        isEditing = false
        currentProductEdit = null
    }

    fun save() {
        if (productName.isBlank() || productType.isBlank() || productPrice.isBlank() || productImageUrl.isBlank()) {
            Toast.makeText(context, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }
        val p = Product(name = productName, type = productType, price = productPrice, imageUrl = productImageUrl)
        if (isEditing && currentProductEdit != null) {
            db.collection("products").document(currentProductEdit!!.id)
                .set(p)
                .addOnSuccessListener {
                    Toast.makeText(context, "Cập nhật thành công! ✓", Toast.LENGTH_SHORT).show()
                    reset()
                }
        } else {
            db.collection("products").add(p)
                .addOnSuccessListener {
                    Toast.makeText(context, "Thêm sản phẩm thành công! ✓", Toast.LENGTH_SHORT).show()
                    reset()
                }
        }
    }

    fun delete(p: Product) {
        db.collection("products").document(p.id).delete()
        if (currentProductEdit?.id == p.id) reset()
    }

    fun edit(p: Product) {
        isEditing = true
        currentProductEdit = p
        productName = p.name
        productType = p.type
        productPrice = p.price
        productImageUrl = p.imageUrl
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Quản Lý Sản Phẩm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                        Text(
                            "Hệ thống quản lý tồn kho",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepPurple
                ),
                modifier = Modifier.shadow(8.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        if (isEditing) "✏️ Chỉnh Sửa Sản Phẩm" else "➕ Thêm Sản Phẩm Mới",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple
                    )
                    Spacer(Modifier.height(16.dp))

                    InputForm(
                        productName, { productName = it },
                        productType, { productType = it },
                        productPrice, { productPrice = it },
                        productImageUrl, { productImageUrl = it },
                        { save() },
                        buttonText,
                        isEditing
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Product List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "📦 Danh Sách Sản Phẩm",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    "${products.size} sản phẩm",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(products) { p ->
                    ProductListItem(p, { edit(p) }, { delete(p) })
                }
            }
        }
    }
}

@Composable
fun InputForm(
    name: String, onName: (String) -> Unit,
    type: String, onType: (String) -> Unit,
    price: String, onPrice: (String) -> Unit,
    imageUrl: String, onImageUrl: (String) -> Unit,
    onSave: () -> Unit,
    buttonText: String,
    isEditing: Boolean
) {
    Column {
        CustomTextField(name, onName, "Tên sản phẩm", "📝")
        Spacer(Modifier.height(12.dp))
        CustomTextField(type, onType, "Loại sản phẩm", "🏷️")
        Spacer(Modifier.height(12.dp))
        CustomTextField(price, onPrice, "Giá bán (VNĐ)", "💰", price = true)
        Spacer(Modifier.height(12.dp))
        CustomTextField(imageUrl, onImageUrl, "URL hình ảnh", "🖼️")
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isEditing) AccentOrange else DeepPurple
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                buttonText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onChange: (String) -> Unit,
    hint: String,
    icon: String,
    price: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = {
            Text(hint, color = Color.Gray.copy(alpha = 0.6f))
        },
        leadingIcon = {
            Text(icon, fontSize = 20.sp)
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (price) KeyboardType.Number else KeyboardType.Text
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DeepPurple,
            unfocusedBorderColor = Color.LightGray,
            focusedLeadingIconColor = DeepPurple,
            unfocusedLeadingIconColor = Color.Gray
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ProductListItem(p: Product, edit: () -> Unit, delete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Card(
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(p.imageUrl),
                        contentDescription = p.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        p.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkText
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${p.price} đ",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = AccentGreen
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        p.type,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .background(LightGray, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Row {
                IconButton(
                    onClick = edit,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        Icons.Filled.Create,
                        contentDescription = "Edit",
                        tint = AccentOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = delete,
                    modifier = Modifier
                        .size(44.dp)
                        .background(SoftPink, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}