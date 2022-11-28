package es.uca.conexionmorada.Entity

import com.google.type.Date

class User {
    private var _iId: Int = 0
    private var _sEmail: String = ""
    private var _sPassword: String = ""
    private var _sName: String = ""
    private var _sSurname: String = ""
    private var _sUsername: String = ""
    private var _sPrestige: String = ""
    private var _sDni: String = ""
    private var _sUrlImg: String = ""
    private var _dDeletedAt: Date? = null

    //constructor
    constructor(iId: Int, sEmail: String, sPassword: String, s_Name: String,
    sSurname: String, sUsername: String, sPrestige: String, sDni: String, sUrlImg: String) {
        this._iId = iId
        this._sEmail = sEmail
        this._sPassword = sPassword
        this._sName = s_Name
        this._sSurname = sSurname
        this._sUsername = sUsername
        this._sPrestige = sPrestige
        this._sDni = sDni
        this._sUrlImg = sUrlImg
    }

    override fun toString(): String {
        return "User(_iId=$_iId, _sEmail='$_sEmail', _sPassword='$_sPassword', _sName='$_sName', _sSurname='$_sSurname', _sUsername='$_sUsername', _sPrestige='$_sPrestige', _sDni='$_sDni', _sUrlImg='$_sUrlImg', _dDeletedAt=$_dDeletedAt)"
    }

}