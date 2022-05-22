package com.lifegate.app.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.lifegate.app.data.db.entities.User
import com.lifegate.app.utils.CURRENT_USER_ID

/*
 *Created by Adithya T Raj on 24-06-2021
*/

@Dao
interface UserDao {

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: User) : Long*/

    @Query("SELECT * FROM user WHERE uid = $CURRENT_USER_ID")
    fun getUser() : LiveData<User>

    /*@Query("DELETE FROM user")
    suspend fun nukeUser()*/

}