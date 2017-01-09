<div class="modal-header">
  <span>
    <i class="fa fa-share-alt"></i>&nbsp;&nbsp;{{vm.shareModalTitle}}</span>
</div>
<div class="modal-body" id="image-share-modal-input">
  <span class="shareModalHighlightCopyTxt show-me">Highlight and Copy link </br></br></span>
  <input size="65" ng-model="vm.imageLink" class="ng-pristine ng-valid ng-touched" autofocus readonly></input>
</div>
<div class="modal-footer">
  <a class="btn btn-primary" ng-href="mailto:someone@theirsite.com?&subject=O2%20Image%20Share&body={{vm.emailLink}}" target="_blank">
        <i class="fa fa-envelope"
           style="cursor: pointer;"></i>&nbsp;Email Link&nbsp;&nbsp;
  </a>&nbsp;&nbsp;

    <a class="btn btn-primary hide-me" ng-href="" ng-click="copyToClipboard(vm.imageLink)" target="_blank">
          <i class="fa fa-clipboard"
             style="cursor: pointer;"></i>&nbsp;Copy Link&nbsp;&nbsp;
    </a>&nbsp;&nbsp;

  <button class="btn btn-warning" type="button" ng-click="vm.close()">Close</button>
</div>
