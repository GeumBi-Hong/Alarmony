package com.slembers.alarmony.network.repository

import androidx.compose.material3.ExperimentalMaterial3Api
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.slembers.alarmony.model.db.ChangeMyInfoRequest
import com.slembers.alarmony.model.db.FindIdRequest
import com.slembers.alarmony.model.db.FindPasswordRequest
import com.slembers.alarmony.model.db.LoginRequest
import com.slembers.alarmony.model.db.Member
import com.slembers.alarmony.model.db.RegistTokenDto
import com.slembers.alarmony.model.db.SignupRequest
import com.slembers.alarmony.model.db.dto.CheckEmailResponseDto
import com.slembers.alarmony.model.db.dto.FindIdResponseDto
import com.slembers.alarmony.model.db.dto.FindPasswordResponseDto
import com.slembers.alarmony.model.db.dto.LoginResponseDto
import com.slembers.alarmony.model.db.dto.SignupResponseDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import com.slembers.alarmony.model.db.dto.CheckIdResponseDto
import com.slembers.alarmony.model.db.dto.CheckNicnameResponseDto
import com.slembers.alarmony.model.db.dto.MyInfoResponse
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.PUT

interface MemberRepository {
    @PUT("members/regist-token")
    suspend fun putRegistToken(
        @Body registTokenDto : RegistTokenDto
    ) : Response<Unit>

    @GET("members")
    fun getMember(
        @Header("token") token : String?
    ) : Call<Member>

    @POST("members/login")
    suspend fun login(
        @Body loginDto : LoginRequest
    ) : Response<LoginResponseDto>

//    회원가입
    @POST("members/sign-up")
    fun signup(
        @Body signupDto : SignupRequest
    ) : Call<SignupResponseDto>



    @POST("members/find-id")
    fun findId(
        @Body findIdDto : FindIdRequest
    ) : Call<FindIdResponseDto>


    @POST("members/find-pw")
    fun findPassword(
        @Body findPasswordDto : FindPasswordRequest
    ) : Call<FindPasswordResponseDto>

    @GET("members/logout")
    fun logOut(
    ) :Call<Unit>

    @GET("members/info")
    fun getMyInfo(
    ): Call<MyInfoResponse>


    @DELETE("member")
    fun signOut(
    ): Call<Unit>

    @GET("members/check-id")
    fun checkId(@Query("username") username: String): Call<CheckIdResponseDto>

    @GET("members/check-email")
    fun checkEmail(@Query("email") email: String): Call<CheckEmailResponseDto>

    @GET("members/check-nickname")
    fun checkNickname(@Query("nickname") nickname: String): Call<CheckNicnameResponseDto>

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
    @PUT("members")
    fun userProfoileEditSubmit(
        @Body userProfoileEditSubmitDto : ChangeMyInfoRequest
    ): Call<Unit>


}