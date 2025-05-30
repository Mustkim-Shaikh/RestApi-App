package com.example.testproject.data.repository



import com.example.testproject.data.model.ApiResponse
import com.example.testproject.data.model.ProductResponse
import com.example.testproject.data.model.SendOtpRequest
import com.example.testproject.data.model.SendOtpResponse
import com.example.testproject.data.model.VerifyOtpRequest
import com.example.testproject.data.model.VerifyOtpResponse
import com.example.testproject.data.remote.AuthApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response


class AuthRepositoryImpl(
    private val api: AuthApi,


) : AuthRepository {

    override suspend fun sendOtp(request: SendOtpRequest): Response<SendOtpResponse> {
        return api.sendOtp(request)

    }


    override suspend fun verifyOtp(request: VerifyOtpRequest): Response<VerifyOtpResponse> {
        return api.verifyOtp(request)
    }


    override suspend fun getProducts(token: String): Response<ProductResponse> {
        return api.getProducts("Bearer $token")
    }

    override suspend fun createTicket(
        token: String,
        ticketType: RequestBody,
        message: RequestBody,
        filePart: MultipartBody.Part?
    ): Response<ApiResponse<Any>> {
        return api.createTicket(token, ticketType, message, filePart)
    }



}