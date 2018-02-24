/* 
** 页面切换的效果控制 
*/
var Msize = $(".m-page").size(), 	//页面的数目
	page_n			= parseInt($("#defaultPage").val()),			//初始页面位置
	initP			= null,			//初值控制值
	initY			= null,			//Y初值控制值
	moveP			= null,			//每次获取到的值
	moveY			= null,			//Y每次获取到的值
	firstP			= null,			//第一次获取的值
	newM			= null,			//重新加载的浮层
	p_b				= null,			//方向控制值
	indexP			= null, 		//控制首页不能直接找转到最后一页
	move			= null,			//触摸能滑动页面
	start			= true, 		//控制动画开始
	startM			= null,			//开始移动
	position		= null,			//方向值
	DNmove			= false,		//其他操作不让页面切换
	canmove			= false,		//首页返回最后一页
	mousedown		= null;			//PC鼠标控制鼠标按下获取值
	v_h	= null;		//记录设备的高度
	
$(window).load(function(){
	$(".p-index").animate({"opacity":1},400,function(){
		var fn_h = function() {
			if(document.compatMode == "BackCompat")
				var Node = document.body;
			else
				var Node = document.documentElement;
			 return Math.max(Node.scrollWidth,Node.clientWidth);
		}
		var page_h = fn_h();
		var m_h = $(".m-page").width();
		page_h >= m_h ? v_h = page_h : v_h = m_h ;
		$(".m-page").eq(page_n-1).removeClass("hide").addClass("show");
		$(".bnrList>li").eq(page_n-1).addClass("z-sel").siblings().removeClass("z-sel");
		if(page_n == 6){
			$(".skipBtn").hide();
		}
	});
});	

$(function(){
    $(".u-radio").click(function(){
        var _this = $(this);
        _this.siblings(".u-hidden").val(_this.find(".label").data("v"));
        _this.siblings(".u-radio").removeClass("u-radioSel");
        _this.addClass("u-radioSel");
    })
//    $(".tipBtn").click(function(){
//        var index = $(".tipBtn").index($(this));
//        $(".popTip").show();
//        $(".tipWrap").hide().eq(index).show();
//        $("#modal").show();
//    })
//    $(".popTip>.closeBtn").click(function(){
//        $(".popTip").hide();
//        $("#modal").hide();
//    })
    $("#skipBtn").click(function(){
		page_n = 6;
		$(".m-page").eq(page_n-1).removeClass("hide").addClass("show");
		$(".bnrList>li").eq(page_n-1).addClass("z-sel").siblings().removeClass("z-sel");
		$(".skipBtn").hide();
	})
	$("#modal,#closeAlert").click(function(){
	    $("#windowConfirm").hide();
	    $("#modal").hide();
	    $(".popTip").hide();
	})
})

var submitLock = false;

function checkForm(){
    if(submitLock){
        return false;
    }
    var age = $("#Age").val().replace(/\s/g, ""),
        gender = $("#Sex").val().replace(/\s/g, ""),
        term = $("#PPP_Sens").val().replace(/\s/g, ""),
        premium = $("#SA").val().replace(/\s/g, "");

    if(typeof(age) !== 'undefined'){
        if(age == ""){
            alert('请输入年龄');
            return false;
        }
        var reg1 = /^([0-9]|(1[0-5]))$/;
        var re1 = new RegExp(reg1);
        if (!re1.test(age)) {
            alert("请输入正确的年龄，0~15");
            return false;
        }
    } 
    if(gender == ""){
        alert('请选择性别');
        return false;
    } 
    if(term == ""){
        alert('请选择缴费年期');
        return false;
    } 
    if(typeof(premium) !== 'undefined'){
        if(premium == ""){
            alert('请填写保额');
            return false;
        }
        var reg2 = /^[0-9]*$/;
        var re2 = new RegExp(reg2);
        if (!re2.test(premium)) {
            alert("保额需要填写正确的数字");
            return false;
        }
    }

    submitLock = true;
    $("#popLoad").addClass("popLoad-show");
}

function alert(cont){ 
    var popElm = $("#windowConfirm");
    var popWidth = popElm.width();
    var popHeight = popElm.height();
    var pageWidth = $(window).width();
    var winHeight = $(window).height();
    var popLeft = (pageWidth - popWidth)/2;
    var popTop = (winHeight - popHeight)/2;
    popElm.css({"left":popLeft+"px","top":popTop+"px"});
    popElm.find(".cont").text(cont);
    $("#windowConfirm").show();
    $("#modal").show();
} 

// 前三个页面的图片延迟加载
setTimeout(function(){
	for(var i=0;i<3;i++){
		var node = $(".m-page").eq(i);
		if(node.length==0) break;
		if(node.find('.lazy-bk').length!=0){
			lazy_change(node);
		}else continue;
	}
},200)

// 加载当前后面第三个
function lazy_bigP(){
	for(var i=3;i<=5;i++){
		var node = $(".m-page").eq(page_n+i-1);
		if(node.length==0) break;
		if(node.find('.lazy-bk').length!=0){
			lazy_change(node);
		}else continue;
	}
}

// 图片延迟替换函数
function lazy_change(node){
	if(node.attr('data-yuyue')=='true'){
		$('.weixin-share').find('.lazy-bk').attr('src',$('.weixin-share').find('.lazy-bk').attr('data-bk'));
	}
	var lazy = node.find('.lazy-bk');
	lazy.each(function(){
		var self = $(this),
			srcImg = self.attr('data-bk');

		$('<img />').on('load',function(){
			if(self.is('img')){
				self.attr('src',srcImg)
			}
			else{
				self.css({
					'background-image'	: 'url('+srcImg+')',
					'background-size'	: 'cover'
				})
			}
			// 判断下面页面进行加载
			for(var i =0;i<$(".m-page").size();i++){
				var page = $(".m-page").eq(i);
				if($(".m-page").find('.lazy-bk').length==0) continue
				else{
					lazy_change(page);
				}
			}
		}).attr("src",srcImg);
		self.removeClass('lazy-bk');
	})	
}

/*
**模版切换页面的效果
*/
//绑定事件
function changeOpen(e){
	$(".m-page").on('mousedown touchstart',page_touchstart);
	$(".m-page").on('mousemove touchmove',page_touchmove);
	$(".m-page").on('mouseup touchend',page_touchend);
};

//取消绑定事件
function changeClose(e){
	$(".m-page").off('mousedown touchstart');
	$(".m-page").off('mousemove touchmove');
	$(".m-page").off('mouseup touchend');
};

//开启事件绑定滑动
changeOpen();

//触摸（鼠标按下）开始函数
function page_touchstart(e){
	if (e.type == "touchstart") {
		initP = window.event.touches[0].pageX;
		initY = window.event.touches[0].pageY;
	} else {
		initP = e.x || e.pageX;
		initY = e.y || e.pageY;
		mousedown = true;
	}
	firstP = initP;	
};

//插件获取触摸的值
function V_start(val){
	initP = val;
	mousedown = true;
	firstP = initP;		
};

//触摸移动（鼠标移动）开始函数
function page_touchmove(e){
	//e.preventDefault();
	e.stopPropagation();	
	$(".player-tip").hide();
	//判断是否开始或者在移动中获取值
	if(start||startM){
		startM = true;
		if (e.type == "touchmove") {
			moveP = window.event.touches[0].pageX;
			moveY = window.event.touches[0].pageY;
		} else { 
			if(mousedown) {moveP = e.x || e.pageX; moveY = e.y || e.pageY;}
		}
		page_n == 1 ? indexP = false : indexP = true ;	//true 为不是第一页 false为第一页
		
		if(Math.abs(moveY - initY) < Math.abs(moveP - initP)){
			e.preventDefault();
		}
	}

	//设置一个页面开始移动
	if(moveP&&startM){
		
		//判断方向并让一个页面出现开始移动
		if(!p_b){
			p_b = true;
			position = moveP - initP > 0 ? true : false;	//true 为向下滑动 false 为向上滑动
			if(position){

			//向下移动
				if(indexP){								
					newM = page_n - 1 ;
					$(".m-page").eq(newM-1).addClass("active").css("left",-v_h);
					move = true ;
				}
				else{
					if(canmove){
						move = true;
						newM = Msize;
						$(".m-page").eq(newM-1).addClass("active").css("left",-v_h);
					}
					else move = false;
				}
						
			}else{
			//向上移动
				if(page_n != Msize){
					if(!indexP) $('.audio_txt').addClass('close');
					newM = page_n + 1 ;
					$(".m-page").eq(newM-1).addClass("active").css("left",v_h);
					move = true ;
				}
				else{
					move = false ;
				}
			} 
		}
		
		//根据移动设置页面的值
		if(!DNmove){
			//滑动带动页面滑动
			if(move){	
				//开启声音
				if($("#car_audio").length>0&&audio_switch_btn&&Math.abs(moveP - firstP)>100){
					$("#car_audio")[0].play();
					audio_loop = true;
				}
			
				//移动中设置页面的值（top）
				start = false;
				var topV = parseInt($(".m-page").eq(newM-1).css("left"));
				$(".m-page").eq(newM-1).css({'left':topV+moveP-initP});	
				initP = moveP;
			}else{
				moveP = null;	
			}
		}else{
			moveP = null;	
		}
	}
};

//触摸结束（鼠标起来或者离开元素）开始函数
function page_touchend(e){	
		
	//结束控制页面
	startM =null;
	p_b = false;
	
	//判断移动的方向
	var move_p;	
	position ? move_p = moveP - firstP > 100 : move_p = firstP - moveP > 100 ;
	if(move){
		//切画页面(移动成功)
		if( move_p && Math.abs(moveP) >5 ){	
			$(".m-page").eq(newM-1).animate({'left':0},300,"easeOutSine",function(){
				success();
			})
		}
		//返回页面(移动失败)
		else if (Math.abs(moveP) >=5){
			position ? $(".m-page").eq(newM-1).animate({'left':-v_h},100,"easeOutSine",function(){
				$(this).removeClass("active");
			}) : $(".m-page").eq(newM-1).animate({'left':v_h},100,"easeOutSine",function(){
				$(this).removeClass("active");
			});
			start = true;
		}
	}
	/* 初始化值 */
	initP		= null,			//初值控制值
	moveP		= null,			//每次获取到的值
	firstP		= null,			//第一次获取的值
	initY		= null,			//Y初值控制值
	moveY		= null,			//Y每次获取到的值
	mousedown	= null;			//取消鼠标按下的控制值
};
/*
** 切换成功的函数
*/
var J_fTxt = $('.J_fTxt');
setTimeout(function(){
	J_fTxt.eq(0).show();
},500);
function success(){
	/*
	** 切换成功回调的函数
	*/							
	//设置页面的出现
	$(".m-page").eq(page_n-1).removeClass("show active").addClass("hide");
	$(".m-page").eq(newM-1).removeClass("active hide").addClass("show");
	$(".bnrList>li").eq(newM-1).addClass("z-sel").siblings().removeClass("z-sel");
	if(newM-1 == 2){
		var t1 = setTimeout(function(){
			$(".formImg>.img2").animate({'opacity':1},600);
		},2000)
	}
	else{
		$(".formImg>.img2").css({'opacity':0});
	}
	if(newM-1 == 5){
		$(".skipBtn").hide();
	}
	else{
		$(".skipBtn").show();
	}
	
	// 滑动成功加载多面的图片
	lazy_bigP();
	
	//重新设置页面移动的控制值
	page_n = newM;
	start = true;
	J_fTxt.eq(page_n-1).show();
	J_fTxt.each(function(k,v){
		if(k!==page_n-1){
			$(this).hide();
		}
	});
	
}

/*var myAudio = $("#playerAudio")[0];
$("#playerBtn").click(function(){
	if (myAudio.paused) {
		myAudio.play();
	} else {
		myAudio.pause();
	}
});

$(myAudio).bind("play",function(){
	$("#playerBtn").removeClass("player-button-stop");
});
$(myAudio).bind("pause",function(){
	$("#playerBtn").addClass("player-button-stop");
});*/