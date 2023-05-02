package com.example.firebaseproject

class StudentModelClass {

    lateinit var id: String
    lateinit var name: String
    lateinit var course: String
    lateinit var address: String
    lateinit var fees: String

    constructor(id: String, name: String, course: String, address: String, fees: String) {
        this.id = id
        this.name = name
        this.course = course
        this.address = address
        this.fees = fees
    }

    constructor() {

    }
}
