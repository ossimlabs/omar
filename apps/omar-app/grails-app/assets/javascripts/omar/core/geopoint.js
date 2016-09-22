GeoPoint = function(lon, lat) {
    switch (typeof(lon)) {
        case 'number':
            this.lonDec = lon;
            this.lonDeg = this.dec2deg(lon, this.MAX_LON, false);
            this.lonDegCard = this.dec2deg(lon, this.MAX_LON, true);

            break;
        case 'string':
            if (this.decode(lon)) { this.lonDeg = lon; }
            this.lonDec = this.deg2dec(lon, this.MAX_LON);
            this.lonDegCard = this.dec2deg(this.lonDec, this.MAX_LON, true);

            break;
    }

    switch (typeof(lat)) {
        case 'number':
            this.latDec = lat;
            this.latDeg = this.dec2deg(lat, this.MAX_LAT, false);
            this.latDegCard = this.dec2deg(lat, this.MAX_LAT, true);

            break;
        case 'string':
            if (this.decode(lat)) { this.latDeg = lat; }
            this.latDec = this.deg2dec(lat, this.MAX_LAT);
            this.latDegCard = this.dec2deg(this.latDec, this.MAX_LAT, true);

            break;
    }
};

GeoPoint.prototype = {

    CHAR_DEG : "\u00B0",
    CHAR_MIN : "\u0027",
    CHAR_SEC : "\u0022",
    CHAR_SEP : "\u0020",

    MAX_LON: 180,
    MAX_LAT: 90,

    // decimal
    lonDec: NaN,
    latDec: NaN,

    // degrees
    lonDeg: NaN,
    latDeg: NaN,

    dec2deg: function(value, max, cardinal) {
        var sign = value < 0 ? -1 : 1;

        var abs = Math.abs(Math.round(value * 1000000));

        if (abs > (max * 1000000)) { return NaN; }
        var dec = abs % 1000000 / 1000000;

        var deg = Math.floor(abs / 1000000);
        if (deg < 10) { deg = "0" + deg; }
        if (max == this.MAX_LON && deg < 100) { deg = "0" + deg; }

        var min = Math.floor(dec * 60);
        if (min < 10) { min = "0" + min; }

        var sec = (dec - min / 60) * 3600;
        sec = sec.toFixed(2);
        if (sec < 10) { sec = "0" + sec; }

        var result = "";
        result += deg;
        result += this.CHAR_DEG;
        result += this.CHAR_SEP;
        result += min;
        result += this.CHAR_MIN;
        result += this.CHAR_SEP;
        result += sec;
        result += this.CHAR_SEC;
        if (cardinal) {
            var direction;
            switch (max) {
                case this.MAX_LAT: direction = sign == -1 ? "S" : "N"; break;
                case this.MAX_LON: direction = sign == -1 ? "W" : "E"; break;
            }
            result += " " + direction;
        }
        else { result = (sign < 0 ? "-" : "") + result; }

        return result;
    },

    deg2dec: function(value) {

        var matches = this.decode(value);

        if (!matches) {
            return NaN;
        }

        var deg = parseFloat(matches[1]);
        var min = parseFloat(matches[2]);
        var sec = parseFloat(matches[3]);

        if (isNaN(deg) || isNaN(min) || isNaN(sec)) {
            return NaN;
        }

        return deg + (min / 60.0) + (sec / 3600);
    },

    decode: function(value) {
        var pattern = "";

        // deg
        pattern += "(-?\\d+)";
        pattern += this.CHAR_DEG;
        pattern += "\\s*";

        // min
        pattern += "(\\d+)";
        pattern += this.CHAR_MIN;
        pattern += "\\s*";

        // sec
        pattern += "(\\d+\\.\\d+)";
        pattern += this.CHAR_SEC;

        return value.match(new RegExp(pattern));
    },

    getLatDec: function() { return this.latDec; },
    getLatDeg: function() { return this.latDeg; },
    getLatDegCard: function() { return this.latDegCard; },
    getLonDec: function() { return this.lonDec; },
    getLonDeg: function() { return this.lonDeg; },
    getLonDegCard: function() { return this.lonDegCard; }
};
