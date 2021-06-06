angular
    .module('kalahComponent',[])
    .component('kalahComponent',{
        templateUrl: 'app/kalah.template.html',
        controller: function KalahController($http, $interval, $filter) {
            let self = this;
             self.handleSuccess = function(message) {
                self.alert.reset();
                self.alert.show = true;
                self.alert.type = 'success';
                self.alert.messages.push(message)
            };

            self.onError = function(response){
                let errors = response.data.error;
                self.alert.reset();
                self.alert.type = 'danger';
                self.alert.show = true;
                self.alert.messages.push("Error: " + errors);
            }

            self.form= {
                title: '',
                isShowAddPlayer: false,
                isShowBoard: true,
                isShowGameMenu: true,
                pits : [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14],
                games : [],
                players : [],
                playerNumber : 2,
                player: null,
                gameId: null,
                isGameOver: false,
                hide: function() {
                    this.isShowAddPlayer = false;
                    this.isShowGameMenu = false;
                },
                show: function() {
                    this.isShowAddPlayer = true;
                }
            };

            self.alert= {
                type: 'success',
                show: false,
                messages : [],
                reset: function() {
                    this.messages = [];
                    this.show = false;
                }
            };

            self.createNewGame = function() {
                $http.post('/games').then(function onSuccess(response){
                    let gameId =  response.data.id;
                    self.form.gameId = gameId;
                    self.handleSuccess("Game has been successfully created with ID: "+gameId)
                    self.form.show();
                },self.onError)
            };

            self.addPlayer = function() {
                $http.put('/games/add/'+self.form.gameId+'/player/'+self.form.player).then(function onSuccess(response){
                    self.form.players = response.data.players;
                    self.form.playerNumber = response.data.playerNumber;
                    self.startTimer();
                    self.form.hide();
                    self.getGames();
                    self.handleSuccess("Player: "+self.form.player+" has been successfully added!")
                },self.onError)
            };

            self.getGames = function(){
                $http.get('/games/list').then(function onSuccess(response){
                    self.form.games = response.data.games;
                },self.onError)
            };
            self.getPlayers = function(){
                $http.post('/games/'+self.form.gameId+'/players').then(function onSuccess(response){
                    self.form.players = response.data.players;
                },self.onError)
            };
            self.isOtherTurn = function(playerNumber){
                if(self.form.players[0] && self.form.players[1] &&
                    self.form.players[playerNumber] === self.form.player &&
                    playerNumber === self.form.playerNumber)
                    return false;
                else
                    return true;
            };
            self.chooseGame = function(gameId){
                self.form.isShowGameMenu = false;
                self.form.gameId = gameId;
                self.form.player = "";
                self.form.show();
            };

            self.move = function(selectedMove){
                $http.put('/games/'+self.form.gameId+'/pits/'+selectedMove).then(function onSuccess(response){
                    self.handleBoardData(response.data);
                    if(!self.isGameOver){
                        self.handleSuccess("Player: "+self.form.player+" played! "+
                            " Selected Move: "+selectedMove+" Game Id:"+self.form.gameId)
                    }
                },self.onError)
            };

            self.handleBoardData = function (data){
                self.form.pits = data.status;
                if(!self.form.pits)
                    self.stopTimer();
                self.form.players = data.players;
                self.form.gameId = data.id;
                self.form.playerNumber = data.playerNumber;
                self.isGameOver = data.isGameOver;
                if(self.isGameOver){
                    self.form.playerNumber = 2;
                    if(self.form.pits[14] > self.form.pits[7])
                        self.handleSuccess("Game Over!! Player: "+self.form.players[1] + " Won!");
                    else if(self.form.pits[14] < self.form.pits[7])
                        self.handleSuccess("Game Over!!  Player: "+self.form.players[0] + " Won!");
                    else
                        self.handleSuccess("Game is Draw!!")
                    self.stopTimer();
                }
            };


            //Initiate the Timer object.
            self.Timer = null;

            //Timer start function.
            self.startTimer = function () {
                //Initialize the Timer to run every 1000 milliseconds i.e. one second.
                self.Timer = $interval(function () {
                    $http.get('/games/'+self.form.gameId+'/boardStatus').then(function onSuccess(response){
                        self.handleBoardData(response.data);
                    },self.onError);
                }, 1000);
            };
            self.stopTimer = function () {
                if (angular.isDefined(self.Timer)) {
                    $interval.cancel(self.Timer);
                }
            };
        }
});

angular.module('kalahApp', [
    'kalahComponent'
]);

