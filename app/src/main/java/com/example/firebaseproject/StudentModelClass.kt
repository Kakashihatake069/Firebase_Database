package com.example.firebaseproject

class StudentModelClass {

    lateinit var id: String
    lateinit var name: String
    lateinit var course: String
    lateinit var address: String
    lateinit var fees: String
    lateinit var image : String

    constructor(id: String, name: String, course: String, address: String, fees: String,image: String) {
        this.id = id
        this.name = name
        this.course = course
        this.address = address
        this.fees = fees
        this.image=image
    }

    constructor() {

    }
}
