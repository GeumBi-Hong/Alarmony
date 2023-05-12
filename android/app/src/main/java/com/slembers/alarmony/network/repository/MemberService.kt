package com.slembers.alarmony.network.repository

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.slembers.alarmony.MainActivity
import com.slembers.alarmony.feature.common.NavItem
import com.slembers.alarmony.feature.ui.common.showDialog
import com.slembers.alarmony.model.db.FindIdRequest
import com.slembers.alarmony.model.db.FindPasswordRequest
import com.slembers.alarmony.model.db.LoginRequest
import com.slembers.alarmony.model.db.RegistTokenDto
import com.slembers.alarmony.model.db.SignupRequest
import com.slembers.alarmony.model.db.TokenReissueRequest
import com.slembers.alarmony.model.db.dto.CheckEmailResponseDto
import com.slembers.alarmony.model.db.dto.CheckIdResponseDto
import com.slembers.alarmony.model.db.dto.CheckNicnameResponseDto
import com.slembers.alarmony.model.db.dto.FindIdResponseDto
import com.slembers.alarmony.model.db.dto.FindPasswordResponseDto
import com.slembers.alarmony.model.db.dto.SignupResponseDto
import com.slembers.alarmony.model.db.dto.TokenReissueResponse
import com.slembers.alarmony.network.api.AlarmonyServer
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@ExperimentalMaterial3Api
@ExperimentalGlideComposeApi
object MemberService {
    val memberApi = AlarmonyServer().memberApi

    suspend fun putRegistTokenAfterSignIn() : Unit {
        Log.d("INFO", "동적으로 가져오는 토큰 정보")
        val token = MainActivity.prefs.getString("registrationToken","")
        if(token.isNotBlank()){
            putRegistToken(token)
        } else {
            Log.d("INFO", "등록 토큰이 존재하지 않습니다.")
        }
    }

    suspend fun putRegistToken( token : String? ) {
        try{
            Log.d("INFO", "함수 도달 성공")
            val tt : String? = MainActivity.prefs.getString("accessToken","")
            Log.d("INFO", "$tt")
            memberApi.putRegistToken(
                RegistTokenDto(
                    registrationToken = token
                )
            )
            Log.i("response","토큰이 전송되었습니다.")
        } catch ( e : Exception) {
            Log.d("disconnection", "등록토큰이 전송되지 못했습니다.")
            println(e.message)
        }
    }

    fun singup(
        request : SignupRequest,
        isSuccess : (Boolean) -> Unit = {}
    ) {
        try {
            Log.d("가입", "signup ==> 회원가입시도")
            memberApi.signup( request ).enqueue(object : Callback<SignupResponseDto> {
                override fun onResponse(call: Call<SignupResponseDto>, response: Response<SignupResponseDto>) {
                    Log.d("response","response")
                    Log.d("response","${response.code()}")
                    if(response.isSuccessful) {
                        Log.d("success", "회원가입 성공!!!")
//                         부모 객체에 "성공"이라는 string을 건네줘야한다. 즉 회원가입이 성공했음을 알려야한다. 어떻게??
                        isSuccess(true)
                        Log.d("success", "${isSuccess}")
                    } else {
                        Log.d("Failed", "회원가입 실패")
                        isSuccess(false)
                    }
                }

                override fun onFailure(call: Call<SignupResponseDto>, t: Throwable) {
                    Log.d("disconnection", "회원가입 실패하였습니다..")
                    Log.d("disconnection", "회원가입 원인 : ${t.message}..")
                    isSuccess(false)
                }
            })
        } catch ( e : Exception ) {
            Log.d("response", "회원가입 아예안됨?")
            println(e.message)
        }
    }




//    email을 보내고 이후의 동작은 추후에 확인



    @OptIn(ExperimentalGlideComposeApi::class)
    @ExperimentalMaterial3Api
    suspend fun login(
        username: String,
        password:String,
        context: Context
    ) : Boolean {


            Log.d("Start", "login --> 로그인 시도")
            val response = memberApi.login(
                LoginRequest(
                    username = username,
                    password = password
                )
            )

            //로그인이 성공하였을경우 -> SUCCESS 200번대
            if (response.isSuccessful) {

                var loginResult = response.body()
                //sharedPreference에 저장
                MainActivity.prefs.setString("accessToken", loginResult?.accessToken)
                MainActivity.prefs.setString("refreshToken", loginResult?.refreshToken)
                MainActivity.prefs.setString("username",username)

                Log.d("login", "[로그인] 성공!")

                return true;
            }
            //로그인이 실패하였을 경우 400번대 에러
            else {
                val duration = Toast.LENGTH_SHORT
                var message = ""

                val jObjError = JSONObject(response.errorBody()!!.string())
                val status = jObjError.getString("status")

                when (status) {
                    "401" -> {
                        message = "아이디와 비밀번호를 다시 확인해주세요."
                    }

                    "403" -> {
                        message = "회원가입 이메일 인증을 완료해주세요"
                    }
                    "404" -> {
                        message = "가입되지 않은 회원입니다."
                    }

                    else -> {
                        message = "내부 서버에 문제가 발생하였습니다. 잠시 후에 다시 시도해주세요"
                    }

                }
                val toast = Toast.makeText(context,message,duration)
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
                return false;
            }
    }

    fun findId(email: String, context: Context, navController: NavController) {
        try {
            Log.d("test","아이디 찾기 위해 이메일 보냄")
            memberApi.findId(
                FindIdRequest(
                    email = email
                )
            ).enqueue(object : Callback<FindIdResponseDto> {
                override fun onResponse(
                    call: Call<FindIdResponseDto>,
                    response: Response<FindIdResponseDto>) {
                    Log.d("response", "${response.body()}")
                    Log.d("response","${response.code()}")
                    Log.d("response","확인")
                    Log.d("response","${call}")
//                    retrofit에서 제공하는 response의 isSuccessful값이 true라면 아래 실행
                    if (response.isSuccessful) {
                        Log.d("response","아이디 찾기위한 이메일 전송 성공")
                        showDialog("알림", "이메일을 보냈어요!", context, navController)
//                  response.body()는 Retrofit에서 HTTP 응답을 처리할 때 사용하는 메서드 중 하나입니다.
//                  이 메서드는 HTTP 응답을 받은 후, HTTP 응답 바디를 T 타입의 객체로 파싱하여 반환합니다.
//                  반환된 객체는 사용자가 원하는 타입으로 변환하여 사용할 수 있습니다.
//                  예를 들어, HTTP 응답 바디가 JSON 형태로 제공되면 이를 Kotlin 객체로 변환하여 사용할 수 있습니다.

                    } else {
                        Log.d("response","아이디 찾기위한 이메일 전송 실패")
                        showDialog("알림", "올바른 이메일을 입력해주세요!", context, navController)
                    }
                }
                //서버 요청이 자체가 실패한 경우 아래 onFailure가 실행된다.
                override fun onFailure(call: Call<FindIdResponseDto>, t: Throwable) {
                    showDialog("알림", "뭔가 잘못됐나봐요", context, navController)
                    Log.d("response", "서버 요청이 실패")
                    Log.d("response", "${t}")
                }
            }

            )
//          try에서 예외가 발생하면 호춯된다.
        } catch ( e: Exception ) {
            Log.d("response", "예외발생")
            showDialog("알림", "뭔가 잘못됐나봐요", context, navController)
        }
    }

    fun findPswd(
        email: String,
        username: String,
        context: Context,
    navController: NavController
    ) {
        try {
            memberApi.findPassword(
                FindPasswordRequest(
                    username = username,
                    email = email,
                )

            ).enqueue(object: Callback<FindPasswordResponseDto> {
                override fun onResponse(call: Call<FindPasswordResponseDto>, response: Response<FindPasswordResponseDto>) {
                    if(response.isSuccessful) {
                        Log.d("response", "비밀번호찾기 신호 성공")
                        showDialog("알림", "임시비밀번호를 전송했어요!", context, navController)
                    } else {
                        Log.d("response", "비밀번호찾기 신호 실패")
                        showDialog("알림", "올바른 정보를 입력해 주세요...", context, navController)
                    }
                }
                override fun onFailure(call: Call<FindPasswordResponseDto>, t: Throwable) {
                    Log.d("fail", "비밀번호찾기 실패")
                    showDialog("알림", "뭔가 잘못됐나봐요", context, navController)
                }
            })
        } catch ( e : Exception ) {
            Log.d("fail", "비밀먼호 찾기 예외 발생")
            showDialog("알림", "뭔가 잘못됐나봐요", context, navController)
            println(e.message)
        }
    }

//    @OptIn(ExperimentalGlideComposeApi::class)
    @ExperimentalMaterial3Api
    fun logOut(context:Context, navController: NavController) {
        try{
            memberApi.logOut().enqueue(object: Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if(response.isSuccessful) {
                        Log.d("response", "로그아웃")
                        Log.d("response", "${response.body()}")
                        Log.d("response", "${response.code()}")
                        showDialog("알림", "로그아웃되었어요!", context, navController)
                        navController.navigate(NavItem.LoginScreen.route)
                        MainActivity.prefs.reset()

                    } else {
                        Log.d("response", "로그아웃")
                        Log.d("response", "${response.body()}")
                        Log.d("response", "${response}")
                        Log.d("response", "${response.code()}")
                        showDialog("알림", "로그아웃실패...", context, navController)
//                        navController.navigate(NavItem.LoginScreen.route)
//                        MainActivity.prefs.reset()
                    }
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("fail", "비밀번호찾기 실패")
                    showDialog("알림", "뭔가 잘못됐나봐요", context, navController)
                }
            })

        } catch(e: Exception) {
            Log.d("fail", "로그아웃 예외 발생")
            showDialog("알림", "뭔가 잘못됐나봐요", context, navController)

        }







//        fun signOut(context:Context, navController: NavController)
    }

    fun checkId(
        username:String,
        isSuccess : (Boolean) -> Unit = {}) {
        try{
            memberApi.checkId(

                username = username
            ).enqueue(object: Callback<CheckIdResponseDto> {
                override fun onResponse(
                    call: Call<CheckIdResponseDto>,
                    response: Response<CheckIdResponseDto>
                ) {
//                    엄밍히 말하면 모든 응답이 성공으로 들어온다.
                    if(response.isSuccessful) {
                        Log.d("response", "아이디체크")
                        Log.d("response", "${username}")
                        Log.d("response", "${response.body()}")
                        if(response.body()?.duplicated == true) {
                            isSuccess(true)
                        } else {
                            isSuccess(false)
                        }

                    } else {
                        Log.d("response", "아이디체크 실패")

                    }
                }

                override fun onFailure(call: Call<CheckIdResponseDto>, t: Throwable) {
                    Log.d("fail", "실패")
                }
            })
        } catch (e: Exception) {
            Log.d("fail","실패2")

        }
    }

    fun checkEmail(email:String,
                   isSuccess : (Boolean) -> Unit = {}) {
        try{
            memberApi.checkEmail(

                email = email
            ).enqueue(object: Callback<CheckEmailResponseDto> {
                override fun onResponse(
                    call: Call<CheckEmailResponseDto>,
                    response: Response<CheckEmailResponseDto>
                ) {
                    if(response.isSuccessful) {
                        Log.d("response", "이메일체크통신")
                        Log.d("response", "${response.body()}")
                        if(response.body()?.duplicated == true) {
                            isSuccess(true)
                        } else {
                            isSuccess(false)
                        }

                    } else {
                        Log.d("response", "이메일 실패")

                    }
                }

                override fun onFailure(call: Call<CheckEmailResponseDto>, t: Throwable) {
                    Log.d("fail", "실패")
                }
            })
        } catch (e: Exception) {
            Log.d("fail","실패2")

        }
    }

    fun checkNickname(nickname:String,
                      isSuccess : (Boolean) -> Unit = {}) {
        try{
            memberApi.checkNickname(

                nickname = nickname
            ).enqueue(object: Callback<CheckNicnameResponseDto> {
                override fun onResponse(
                    call: Call<CheckNicnameResponseDto>,
                    response: Response<CheckNicnameResponseDto>
                ) {
                    if(response.isSuccessful) {
                        Log.d("response", "닉네임체크")
                        Log.d("response", "${nickname}")
                        Log.d("response", "${response.body()}")
                        if(response.body()?.duplicated == true) {
                            isSuccess(true)
                        } else {
                            isSuccess(false)
                        }

                    } else {
                        Log.d("response", "닉네임 실패")

                    }
                }

                override fun onFailure(call: Call<CheckNicnameResponseDto>, t: Throwable) {
                    Log.d("fail", "실패")
                }
            })
        } catch (e: Exception) {
            Log.d("fail","실패2")

        }
    }

    /**
     * 토큰 재발급
     */
    suspend fun reissueToken(username: String, refreshToken: String) :Boolean {

        Log.d("refresh","토큰 재발급")
        // 토큰 재발급 API 호출
        val response = memberApi.refresh(TokenReissueRequest(
            grantType = "Bearer",
            username = username,
            refreshToken = refreshToken,
        ))
        //성공 200~300번
        //성공 시에는 재발급 받은 토큰을 sharedPreference에 저장한다.
        if (response.isSuccessful) {
            Log.d("refresh","액세스 토큰"+response.body()?.accessToken)
            Log.d("refresh","리프레시 토큰"+response.body()?.refreshToken)

            MainActivity.prefs.setString("accessToken", response.body()?.accessToken)
            MainActivity.prefs.setString("refreshToken", response.body()?.refreshToken)

            return true;
        }else{
            //실패 시에는 로그인 만료 메세지를 보내주고 로그아웃 시킨다.
            return false
        }
        //실패 400번
    }
}

