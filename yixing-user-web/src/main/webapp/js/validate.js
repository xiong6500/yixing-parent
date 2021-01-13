/**
 * 判断字符串是否为空
 * @str 校验字符串
 * @return	校验通过返回true,否则返回false
 * */
function is_empty(str) {
    if ((str == null) || (str.length == 0) || trim(str).length ==0){
    	return true;
    }else{
    	return false;
    }
}

/**
 * 去除前后空格
 * @param str
 * @returns
 */
function trim(str){   
    str = str.replace(/^(\s|\u00A0)+/,'');   
    for(var i=str.length-1; i>=0; i--){   
        if(/\S/.test(str.charAt(i))){   
            str = str.substring(0, i+1);   
            break;   
        }   
    }   
    return str;
}

/**
 * 判断字符串长度，对于一个汉字则返回按２个字节处理
 * @s 校验字符串
 * @return	长度
 * */
function get_length(s) {
	var sLength = 0;
	
	for (var i = 0; i < s.length; i++) {
		var c = s.charAt(i);
		if (c.charCodeAt(0) < 256)
			sLength += 1;
		else
			sLength += 2;
	}
	return sLength;
}

/**
 * 判断对象的值是否为数字
 * @num 校验字符串
 * @return	校验通过返回true,否则返回false
 * */
function is_number(num) {
	if((num==null)||(num=="")) {
	 	return true;
	}
	if(isNaN(num)) {
		return false;
	}else {
		return true;
	}
}

/**
 * 传真，电话号码验证(允许为空)
 * @str 校验字符串
 * @return	校验通过返回true,否则返回false
 * */
function is_phone(phone) {
	/**
		规则：区号第1位为0
			区号第2位为自然数
			区号第3-4位为0-9的整数
			========================
			区号后间隔符"-"隔开
			========================
			电话号码第1位为自然数
			电话号码第2以后为0-9整数
	*/
	if(is_empty(phone)) {
		return true;
	}
	var reg=/^(0)([1-9])(\d)(\-)([1-9])(\d{7})$|(0)([1-9])(\d{2})(\-)([1-9])(\d{6,7})$/;
	if(!reg.test(phone)) {
		return false;
	} else {
		return true;
	}
		
		
}

/**
* 移动电话号码规则验证(允许为空)
* @mobile 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_mobile(mobile) {   

	if (is_empty(mobile)) {
			return true;
	}

	var reg=/^(18|13|15|17|14)(\d{9})$/;
	
	/**
			规则：移动电话号码第1-2位为13|15|18开头
				=========================
				其余为0-9的整数
	*/
	
	if(reg.test(mobile)) {
		return true;
	} else {
		return false;
	}
}

/**
* 判断是否为有效的mail地址(允许为空)
* @email 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_email(email) {
	if (is_empty(email)) {
		return true;
	}
	var reg =  /^(?:[a-zA-Z0-9]+[_\-\+\.]?)*[a-zA-Z0-9]+@(?:([a-zA-Z0-9]+[_\-]?)*[a-zA-Z0-9]+\.)+([a-zA-Z]{2,})+$/gi;
	if (reg.test(email)) {
		return true;
	}
	else {
		return false;
	}
}

/**
* 检查是否包含非法字符\+,;/*<>“.`@%&￥‘()^=!
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_illegal_char(str){
	if(is_illegal_char_reg(str)) {
		return true ;
	}
	if((str.indexOf("*") != -1) || (str.indexOf(".") != -1)) {
	   return true;
	}
	
	return false;
}

/**
* 检查是否包含非法字符  (允许输入*)
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_illegal_char_reg(str){
	if(str.indexOf("<") > -1||str.indexOf(">") > -1 ||
       str.indexOf("\"") > -1||str.indexOf("&") > -1||
       str.indexOf("'") > -1||str.indexOf(" ") > -1 ||
       str.indexOf("+") > -1||str.indexOf(",") > -1||
       str.indexOf(";") > -1||str.indexOf("/") > -1||
       str.indexOf("`") > -1||str.indexOf("@") > -1||
       str.indexOf("%") > -1||str.indexOf("￥") > -1||
       str.indexOf("’") > -1||str.indexOf("”") > -1||
       str.indexOf("‘") > -1||str.indexOf("“") > -1||
       str.indexOf("#") > -1||str.indexOf("$") > -1||
       str.indexOf("^") > -1||str.indexOf("=") > -1||
       str.indexOf("!") > -1||str.indexOf("(") > -1||
       str.indexOf(")") > -1||str.indexOf('~') > -1||
       str.indexOf("|") > -1||str.indexOf('?') > -1||
       str.indexOf("\\")> -1){
		   return true;
    }
    return false;
}

/**
* 检查是否包含非法字符
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_illegal_char_reg1(str){
	if(str.indexOf("<") > -1||str.indexOf(">") > -1 ||
       str.indexOf("\"") > -1||str.indexOf("&") > -1 ||
       str.indexOf("'") > -1||str.indexOf(" ") > -1 ||
       str.indexOf("+") > -1||str.indexOf(",") > -1||
       str.indexOf(";") > -1||str.indexOf("/") > -1||
       str.indexOf("`") > -1||str.indexOf("@") > -1||
       str.indexOf("%") > -1||str.indexOf("￥") > -1||
       str.indexOf("’") > -1||str.indexOf("”") > -1||
       str.indexOf("‘") > -1||str.indexOf("“") > -1||
       str.indexOf("#") > -1||str.indexOf("$") > -1||
       str.indexOf("^") > -1||str.indexOf("=") > -1||
       str.indexOf("!") > -1||str.indexOf("*") > -1||
       str.indexOf("(") > -1||str.indexOf(")") > -1||
       str.indexOf('~') > -1||str.indexOf("|") > -1||
       str.indexOf('?') > -1||str.indexOf("\\")> -1) {
		   return true;
    }
    return false;
}

//检查输入框非法字符
function checkLawlessStr(str){
    if(str.indexOf("<") > -1||str.indexOf(">") > -1 ||
       str.indexOf("\"") > -1||str.indexOf("&") > -1||
       str.indexOf("'") > -1||str.indexOf(" ") > -1||
       str.indexOf("’") > -1||str.indexOf("”") > -1||
       str.indexOf("‘") > -1||str.indexOf("“") > -1){
        return false;
    }
    return true;
}

function check_sso_rules(str){
    if(str.indexOf("<") > -1||str.indexOf(">") > -1 ||
       str.indexOf("&") > -1||str.indexOf("'") > -1 ||
       str.indexOf("’") > -1||str.indexOf("”") > -1 ||
       str.indexOf("‘") > -1||str.indexOf("“") > -1 ||
       str.indexOf("\"") > -1||str.indexOf("\\") > -1){
        return false;
    }
    return true;
}


/**
* 检查内容是否满足长度限制
* @str 校验字符串
* @max 字符串最大长度
* @return	校验通过返回true,否则返回false
* */
function is_limit_len(str,max) {
	var len = get_length(str);
	
    if(len>max) {
        return false;
    }
    return true;
}

/**
* 校验一个字符串是否符合IP地址得格式(兼容ipv4/ipv6)
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_ip(str) {
	var rs = false;
	
	rs = is_ip_v4(str);
	if(!rs) {
		rs = is_ip_v6(str);
	}
	return rs;
}


/**
 * 验证是否是域名
 */
function is_domain_name(str) {
	var temprule = /[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?$/;

	if (temprule.test(str)){
		return true;
	}
	return false;
}


/**
* 校验一个字符串是否符合ipv4地址得格式
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_ip_v4(str) {
   var rs = true;
   
   if(str == null || str.length ==0){
		return false; 
   }
   var reg=/^((\d+)\.(\d+)\.(\d+)\.(\d+))$/;
   if(reg.test(str)) {
       var ary = str.split('.');
       for(var i=0;i<ary.length;i++){
          if(ary[i]>255 || ary[i]<0){
             rs = false;
             break;
          }
       }
       if(rs){
         if(ary!=null && ary.length==4){
             if(ary[0]==0 || ary[3]==0){
                 rs = false;
             }else {
                 rs = true;
             }
         }
      }
    } else {
         rs = false;
    }

    if(!rs){
      return false;
    }else{
      return true;
    }
}

/**
* 校验一个字符串是否符合ipv6地址得格式
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_ip_v6(str) {  
	
	if(str == null || str.length ==0){
		return false; 
	}

    //CDCD:910A:2222:5498:8475:1111:3900:2020   格式

    var patrn=/^([0-9a-f]{1,4}:){7}[0-9a-f]{1,4}$/i;   

    var r=patrn.exec(str);

    if(r) {  
    	
        return true;  
    }  

    if(str=="::"){  

        return true;  
    }  

    //F:F:F::1:1 F:F:F:F:F::1 F::F:F:F:F:1格式  

    patrn=/^(([0-9a-f]{1,4}:){0,6})((:[0-9a-f]{1,4}){0,6})$/i;   

    r=patrn.exec(str);  

    if(r) {      

        var c=cLength(str);  

        if(c<=7 && c>0){  

            return true;  

        }  

    }                  

    //F:F:10F::  格式

    patrn=/^([0-9a-f]{1,4}:){1,7}:$/i;   

    r=patrn.exec(str);  

    if(r) {  

        return true;  

    }  

    //::F:F:10F  格式

    patrn=/^:(:[0-9a-f]{1,4}){1,7}$/i;   

    r=patrn.exec(str);  

    if(r){  

        return true;  

    }  

    //F:0:0:0:0:0:10.0.0.1格式  

    patrn=/^([0-9a-f]{1,4}:){6}(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/i;   

    r=patrn.exec(str);  

    if(r){  

        if(r[2]<=255 && r[3]<=255 &&r[4]<=255 && r[5]<=255 )  

        return true;  

    }  

    //F::10.0.0.1格式  

    patrn=/^([0-9a-f]{1,4}:){1,5}:(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/i;   

    r=patrn.exec(str);  

    if(r) {  

        if(r[2]<=255 && r[3]<=255 &&r[4]<=255 && r[5]<=255 )  

            return true;  

    }  

    //::10.0.0.1格式  

    patrn=/^::(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/i;   

    r=patrn.exec(str);  

    if(r){  

        if(r[1]<=255 && r[2]<=255 &&r[3]<=255 && r[4]<=255)  

            return true;  
    }  
	
	 //::F.10.0.0.1格式 
	patrn=/^::([0-9a-f]{1,4})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/i;   

    r=patrn.exec(str);  
    if(r) {  

        if(r[2]<=255 && r[3]<=255 &&r[4]<=255 && r[5]<=255)  

            return true;  

    }
	 
    return false;  
}

/**
* 校验一个字符串是否符合端口得格式
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_port(port) {
    //return is_number(port) && parseInt(port) > 0 && parseInt(port)<= 65536;
	return /^\d+$/.test(port.value) && isInteger(port) && parseInt(port.value) > 0 && parseInt(port.value)<= 65536;
}

function isInteger(obj) {
    if((obj.value==null)||(obj.value=="")) return true; 
	if(isNaN(obj.value) || (obj.value.indexOf('.') !=-1) || (obj.value.lastIndexOf('-') !=-1))
	{
		return false;
	}else{
		return true;
	}
}//end function

/**
* 校验一个字符串是否为有效天数
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_count(str) {
    return is_number(str) && parseInt(str) > 0;
}

/**
* 校验一个字符串是否包含中文或全角字符
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_chinese(str) {
	var reg = /[\u4E00-\u9FA5]|[\uFE30-\uFFA0]/gi;
	
	if (reg.test(str)) {
		return true;
	}
	return false;
}

/**
* 校验一个字符串是否为正确格式的MAC地址
* @str 校验字符串
* @return	校验通过返回true,否则返回false
* */
function is_mac(str){	
	var temp = /[A-Fa-f0-9]{2}-[A-Fa-f0-9]{2}-[A-Fa-f0-9]{2}-[A-Fa-f0-9]{2}-[A-Fa-f0-9]{2}-[A-Fa-f0-9]{2}/;
	
	if (temp.test(str))
	{
	     return true;
	}
	return false;
}

/**
 * 是否选中复选框
 * @checkbox 复选框名字
 * @return	校验通过返回true,否则返回false
 */
function is_checked(checkbox) {
	
	var checkboxs = document.getElementsByName(checkbox) ;
	
	for(var i = 0 ; i < checkboxs.length ; i++ ){
		if(checkboxs[i].checked){
			return true;
		}
	}
	
	return false ;
	
}

