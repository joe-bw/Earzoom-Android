package kr.co.sorizava.asrplayerKt.Model

import android.content.Context
import android.text.style.BackgroundColorSpan
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.internal.Mutable
import kr.co.sorizava.asrplayerKt.AppConfig
import kr.co.sorizava.asrplayerKt.helper.PlayerCaptionHelper

class BrowserFragmentModel {

    //자막을 위한 define
    var MAX_SPEAKER_NUM = 10
    var mSpeakerColorList : MutableList<Color>? = mutableListOf<Color>()

    //싱글톤
    //화자 텍스트 구조체(화자가 바뀌기전 텍스트를 저장)
    val mPreCSSpeakerText_MutableLiveData : MutableLiveData<MutableList<csSpeakerText>> = MutableLiveData<MutableList<csSpeakerText>>()   //이전 저장값
    val mPreCSSpeakerText_LiveData : LiveData<MutableList<csSpeakerText>> //= mPreCSSpeakerText_MutableLiveData!!
        get() = mPreCSSpeakerText_MutableLiveData

    val mNowCSSpeakerText_MutableLiveData : MutableLiveData<csSpeakerText> = MutableLiveData<csSpeakerText>()    //현재 들어온 최신값
    val mNowCSSpeakerText_LiveData : LiveData<csSpeakerText> //= mNowCSSpeakerText_MutableLiveData!!
        get() = mNowCSSpeakerText_MutableLiveData
/*
    val mNowCSSpeakerText_MutableLiveData : MutableLiveData<csSpeakerText_One> = MutableLiveData<csSpeakerText_One>()    //현재 들어온 최신값
    val mNowCSSpeakerText_LiveData : LiveData<csSpeakerText_One> //= mNowCSSpeakerText_MutableLiveData!!
        get() = mNowCSSpeakerText_MutableLiveData
*/

    //초기화
    constructor()
    {
        mSpeakerColorList!!.add(Color.Black)
        mSpeakerColorList!!.add(Color.Blue)
        mSpeakerColorList!!.add(Color.Cyan)
        mSpeakerColorList!!.add(Color.Gray )
        mSpeakerColorList!!.add(Color.Green)
        mSpeakerColorList!!.add(Color.DarkGray)
        mSpeakerColorList!!.add(Color.LightGray)
        mSpeakerColorList!!.add(Color.Magenta)
        mSpeakerColorList!!.add(Color.Yellow)
        mSpeakerColorList!!.add(Color.Red)

        //mPreCSSpeakerText_MutableLiveData = MutableLiveData<MutableList<csSpeakerText>>()
        mPreCSSpeakerText_MutableLiveData!!.value = mutableListOf<csSpeakerText>()
        mNowCSSpeakerText_MutableLiveData!!.value = csSpeakerText()
        //mNowCSSpeakerText_MutableLiveData!!.value = csSpeakerText_One()
        /*
        if( mPreCSSpeakerText_MutableLiveData.hashCode() == mNowCSSpeakerText_MutableLiveData.hashCode())
        {
            var b = 1
            b=2
        }

        val a = 1

         */
    }



    fun SetText(_transcript : String? , _isFinal : Boolean = false, _speakerNum : Int = 0)
    {


        if( _isFinal == true){
            //2. 이제까지 확정된 화자의 정보를 저장한다.
            //val temp =csSpeakerText(_speakerNum!! ,"화자" + _speakerNum + ":" + _transcript + "\n" )
            //mPreCSSpeakerText_MutableLiveData!!.value!!.add(temp)
            mPreCSSpeakerText_MutableLiveData!!.value!!.add(object : csSpeakerText(_speakerNum!! ,"화자" + _speakerNum + ":" + _transcript + "\n" ,mSpeakerColorList!![_speakerNum!!]){})
            mNowCSSpeakerText_MutableLiveData!!.value?.mText =""
        }
        else
        {
            //1. 들어온 텍스트화면에 노출한다.
            mNowCSSpeakerText_MutableLiveData!!.value!!.setData( _speakerNum, "화자" + _speakerNum + ":" + _transcript)
            //mNowCSSpeakerText_MutableLiveData!!.value!!.setData( _speakerNum, "화자" + _speakerNum + ":" + _transcript)

        }
    }


    //두개를 동일한 함수를 쓰니. 이유는 모르겠으나.
    //      mPreCSSpeakerText_MutableLiveData!!.value = mutableListOf<csSpeakerText>()
    //      mNowCSSpeakerText_MutableLiveData!!.value = csSpeakerText()
    //      의 주소값이 동일하게 할당이 된다. 그래서 차선책으로 클래스 자체를 나눈다.
    // 글자마다 화자와 글자색을의 구조체. spannable의 setSpan을 사용하여 글자색을 입혀주기위함
    open inner class csSpeakerText(var mSpeakerNum :Int =0, var mText : String? ="" , var mTextBackGround : Color = Color.Black) {

        fun setData(_mSpeakerNum :Int =0, _mText : String? ="") {
            if (_mSpeakerNum < 0 || _mSpeakerNum > mSpeakerColorList!!.size) mSpeakerNum =0
            else mSpeakerNum = _mSpeakerNum
            mText = _mText
            mTextBackGround = mSpeakerColorList!![_mSpeakerNum]
        }

    }
/*
    // 글자마다 화자와 글자색을의 구조체. spannable의 setSpan을 사용하여 글자색을 입혀주기위함
    open inner class csSpeakerText_One(var mSpeakerNum :Int =0, var mText : String? ="" , var mTextBackGround : Color = Color.Black) {

        fun setData(_mSpeakerNum :Int =0, _mText : String? ="") {
            if (_mSpeakerNum < 0 || _mSpeakerNum > mSpeakerColorList!!.size) mSpeakerNum =0
            else mSpeakerNum = _mSpeakerNum
            mText = _mText
            mTextBackGround = mSpeakerColorList!![_mSpeakerNum]
        }

    }
*/


    /*
    //싱글톤
    companion object
    {
        //자막을 위한 define
        var MAX_SPEAKER_NUM = 10
        var mSpeakerColorList : MutableList<Color>? = mutableListOf<Color>()

        //싱글톤
        private var instance : BrowserFragmentModel? = null
        fun getInstance(): BrowserFragmentModel
        {
            if( instance == null)
            {
                instance = BrowserFragmentModel()
                init()
            }
            return instance!!
        }

        //화자 텍스트 구조체(화자가 바뀌기전 텍스트를 저장)
        val mPreCSSpeakerText_MutableLiveData : MutableLiveData<MutableList<csSpeakerText>> = MutableLiveData<MutableList<csSpeakerText>>()   //이전 저장값
        val mPreCSSpeakerText_LiveData : LiveData<MutableList<csSpeakerText>> //= mPreCSSpeakerText_MutableLiveData!!
            get() = mPreCSSpeakerText_MutableLiveData

        val mNowCSSpeakerText_MutableLiveData : MutableLiveData<csSpeakerText> = MutableLiveData<csSpeakerText>()    //현재 들어온 최신값
        val mNowCSSpeakerText_LiveData : LiveData<csSpeakerText> //= mNowCSSpeakerText_MutableLiveData!!
            get() = mNowCSSpeakerText_MutableLiveData


        //초기화
        fun init()
        {
            mSpeakerColorList!!.add(Color.Black)
            mSpeakerColorList!!.add(Color.Blue)
            mSpeakerColorList!!.add(Color.Cyan)
            mSpeakerColorList!!.add(Color.Gray )
            mSpeakerColorList!!.add(Color.Green)
            mSpeakerColorList!!.add(Color.DarkGray)
            mSpeakerColorList!!.add(Color.LightGray)
            mSpeakerColorList!!.add(Color.Magenta)
            mSpeakerColorList!!.add(Color.Yellow)
            mSpeakerColorList!!.add(Color.Red)

            //mPreCSSpeakerText_MutableLiveData = MutableLiveData<MutableList<csSpeakerText>>()
            mPreCSSpeakerText_MutableLiveData!!.value = mutableListOf<csSpeakerText>()


            mNowCSSpeakerText_MutableLiveData!!.value = csSpeakerText()
            val a = 1
        }



        fun SetText(_transcript : String? , _isFinal : Boolean = false, _speakerNum : Int = 0)
        {
            if( _isFinal == true){
                //2. 이제까지 확정된 화자의 정보를 저장한다.
                mPreCSSpeakerText_MutableLiveData!!.value!!.add(object : csSpeakerText(_speakerNum!! ,"화자" + _speakerNum + ":" + _transcript + "\n" ){})
            }
            else
            {
                //1. 들어온 텍스트화면에 노출한다.
                mNowCSSpeakerText_MutableLiveData!!.value!!.setData( _speakerNum, "화자" + _speakerNum + ":" + _transcript)
            }
        }
    }

    // 글자마다 화자와 글자색을의 구조체. spannable의 setSpan을 사용하여 글자색을 입혀주기위함
    open class csSpeakerText(var mSpeakerNum :Int =0, var mText : String? ="" , var mTextBackGround : Color = Color.Black) {

        fun setData(_mSpeakerNum :Int =0, _mText : String? ="") {
            if (_mSpeakerNum < 0 || _mSpeakerNum > mSpeakerColorList!!.size) mSpeakerNum =0
            else mSpeakerNum = _mSpeakerNum
            mText = _mText
            mTextBackGround = mSpeakerColorList!![_mSpeakerNum]
        }

    }
*/

}