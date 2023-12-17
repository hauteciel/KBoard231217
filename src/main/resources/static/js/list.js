$(function(){
    // 페이징 헤더
    $("[name='pageRows']").change(function(){
        //alert($(this).val());  // 확인용

        // var frm = $("[name='frmPageRows']")
        // frm.attr("method", "POST")
        // frm.attr("action", "pageRows")
        // frm.submit();

        $("[name='frmPageRows']").attr({
            "method": "POST",
            "action": "pageRows",
        }).submit();
    });
});