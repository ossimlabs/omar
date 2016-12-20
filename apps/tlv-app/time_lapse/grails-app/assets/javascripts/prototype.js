if (!("capitalize" in String.prototype)) {
	String.prototype.capitalize = function() { return this.charAt(0).toUpperCase() + this.slice(1); }
}

if (!("contains" in Array.prototype)) {
	Array.prototype.contains = function(value) {
		for (var i = 0; i < this.length; i++) {
			if(this[i] === value) { return true; }
    		}


		return false;
	}
}

if (!("contains" in String.prototype)) {
	String.prototype.contains = function(string) { return this.search(string) > -1; }
}

if (!("generateFilename" in Date.prototype)) {
	Date.prototype.generateFilename = function() {
		var doubleDigitString = function(number) { return number < 10 ? "0" + number : number.toString(); }

		var year = this.getFullYear();
		var month = doubleDigitString(this.getMonth() + 1);
		var day = doubleDigitString(this.getDate());
		var hour = doubleDigitString(this.getHours());
		var minute = doubleDigitString(this.getMinutes());
		var second = doubleDigitString(this.getSeconds());


		return year + month + day + hour + minute + second;
	}
}

if (!("unique" in Array.prototype)) {
	Array.prototype.unique = function() {
		var array = [];
		for (var i = 0; i < this.length; i++) {
			if(!array.contains(this[i])) {
				array.push(this[i]);
			}
		}


		return array;
	}
}
