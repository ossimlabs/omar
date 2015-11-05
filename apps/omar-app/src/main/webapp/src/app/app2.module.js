angular
    .module('omarApp',['ui.router'])
    .config(function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/home');

        $stateProvider

            // HOME STATES AND NESTED VIEWS ========================================
            .state('home', {
                url: '/home',
                templateUrl: 'home/home.partial.html'
            })

            // nested list with custom controller
            .state('home.list', {
                url: '/list',
                templateUrl: 'home/home-list.partial.html',
                controller: function($scope) {
                    $scope.dogs = ['Bernese', 'Husky', 'Goldendoodle'];
                }
            })

            // nested list with just some random string data
            .state('home.paragraph', {
                url: '/paragraph',
                template: 'I could sure use a drink right now.'
            })

            //// ABOUT PAGE AND MULTIPLE NAMED VIEWS
            //.state('about', {
            //
            //    views: {
            //
            //        // the main template will be place here (realtively named)
            //        '': { templateUrl: 'about/about.partial.html'},
            //
            //        // the child view will be definied here (absolutely named)
            //        'columnOne@about': { template: 'Look I am a column!'},
            //
            //        // for column two, we'll define a separate controller
            //        'columnTwo@about': {
            //            templateUrl: 'about/table-data.html',
            //            controller: 'scotchController'
            //        }
            //    }
            //
            //})

            .state('map', {

                url: '/map/?mapParams&maxFeatures&cql&suggestion',
                templateUrl: 'map/map.partial.html'

            });
    }); // closes $routerApp.config()

    //.controller('scotchController', function($scope){
    //
    //    $scope.message = 'test';
    //
    //    $scope.scotches = [
    //        {
    //            name: 'Macallan 12',
    //            price: 50
    //        },
    //        {
    //            name: 'Chivas Regal Royal Salute',
    //            price: 10000
    //        },
    //        {
    //            name: 'Glenfiddich 1937',
    //            price: 20000
    //        }
    //    ];
    //
    //})