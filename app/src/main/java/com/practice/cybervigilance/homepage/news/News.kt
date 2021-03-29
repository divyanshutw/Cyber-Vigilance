package com.practice.cybervigilance.homepage.news

data class News (
    val category:String?,
    val type:String?,
    val imageUrl:String?,
    val title:String?,
    val description:String?,
    val videoUrl:String?,
    val contributedBy:String?,
    val datePosted:Long?,
    val shares:Long?,
    val likes:Long?,
    val id:String?
)