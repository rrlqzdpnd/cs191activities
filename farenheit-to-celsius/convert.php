<?php

    class Convert   {
        private $_temperature;

        function __construct($temperature = null)   {
            if(!is_null($temperature) && is_numeric($temperature))
                $this->_temperature = $temperature;
        }

        public function setTemp($temperature)   {
            $this->_temperature = $temperature;
        }

        public function convertToCelsius()   {
            if(!isset($this->_temperature) || !is_numeric($this->_temperature))
                return false;
            return ($this->_temperature - 32)*(5.0/9.0);
        }

        public function convertToFahrenheit()   {
            if(!isset($this->_temperature) || !is_numeric($this->_temperature))
                return false;
            return ($this->_temperature * (9.0/5.0)) + 32;
        }
    }

?>
