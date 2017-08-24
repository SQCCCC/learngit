
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">I changed this</b></li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body">

<#list ResultList as anything>
${anything.userLoginId}  - ${anything.enabled!}
<br />
<br />
</#list>


    </div>
</div>