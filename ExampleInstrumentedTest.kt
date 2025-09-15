package com.example.firstkotlin

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

fun main() {
    // ------------------ Phần 1: Khai báo và sử dụng biến ------------------
    val storeName = "Pet Store"
    var petCount = 10

    println("Chao ban den voi $storeName! $storeName hien co $petCount thu cung.")

    // ------------------ Phần 2: Viết hàm ------------------
    fun addPet(petName: String, petType: String) {
        println("Them thu cung moi $petName loai $petType.")
        petCount++ // tăng số lượng thú cưng
    }

    // Gọi hàm addPet 2 lần
    addPet("Mimi", "Cat")
    addPet("Kiki", "Bird")

    // ------------------ Phần 3: Điều khiển luồng chương trình ------------------
    if (petCount >= 2) {
        println("$storeName co du loai thu cung. So luong hien tai la: $petCount")
    } else {
        println("Them thu cung.")
    }

    // ------------------ Phần 4: Tạo lớp (Class) ------------------
    class Pet(val name: String, val type: String)

    val myPet = Pet("Buddy", "Dog")
    println("Thong tin thu cung: Ten - ${myPet.name}, Loai - ${myPet.type}")
}
