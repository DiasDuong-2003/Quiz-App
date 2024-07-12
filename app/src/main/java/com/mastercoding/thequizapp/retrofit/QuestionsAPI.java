package com.mastercoding.thequizapp.retrofit;

import com.mastercoding.thequizapp.model.QuestionList;

import retrofit2.Call;
import retrofit2.http.GET;
// sử dụng để xác định structure và behavior của
// các network requests đến một RESTful API.
// như 1 cầu nối giữa App và Web Service
public interface QuestionsAPI {


    @GET("my_quiz_api.php")  // end point
    Call<QuestionList> getQuestions();


}
