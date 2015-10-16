//angular.module('showcase.withAjax', ['datatables']).controller('WithAjaxCtrl', WithAjaxCtrl);
//
//function WithAjaxCtrl(DTOptionsBuilder, DTColumnBuilder) {
//    var vm = this;
//    vm.dtOptions = DTOptionsBuilder.fromSource('data.json')
//        .withPaginationType('full_numbers');
//    vm.dtColumns = [
//        DTColumnBuilder.newColumn('id').withTitle('ID'),
//        DTColumnBuilder.newColumn('firstName').withTitle('First name'),
//        DTColumnBuilder.newColumn('lastName').withTitle('Last name').notVisible()
//    ];
//}
//
angular.module('showcase.withOptions', ['datatables']).controller('WithOptionsCtrl', WithOptionsCtrl);

function WithOptionsCtrl(DTOptionsBuilder, DTColumnDefBuilder) {
    var vm = this;
    vm.dtOptions = DTOptionsBuilder.newOptions()
        .withPaginationType('full_numbers')
        .withDisplayLength(2)
        .withDOM('pitrfl');
    vm.dtColumnDefs = [
        DTColumnDefBuilder.newColumnDef(0),
        DTColumnDefBuilder.newColumnDef(1).notVisible(),
        DTColumnDefBuilder.newColumnDef(2).notSortable()
    ];
}
