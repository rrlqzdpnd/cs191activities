<?php
    
    require_once('simpletest/autorun.php');
    require_once('convert.php');

    class ConvertTest extends UnitTestCase {
        function testCorrectConversion()   {
            $convert = new Convert(3);
            $val = $convert->convertToCelsius();
            $this->assertIsA($val, 'float');

            $val = $convert->convertToFahrenheit();
            $this->assertIsA($val, 'float');

            $convert2 = new Convert();
            $convert2->setTemp(25);

            $val = $convert2->convertToCelsius();
            $this->assertIsA($val, 'float');

            $val = $convert2->convertToFahrenheit();
            $this->assertIsA($val, 'float');
        }

        function testWhenInputIsNull()  {
            $convert = new Convert();

            $val = $convert->convertToCelsius();
            $this->assertFalse($val);

            $val = $convert->convertToFahrenheit();
            $this->assertFalse($val);
        }


        function testWhenInputIsString()  {
            $convert = new Convert("LOL");

            $val = $convert->convertToCelsius();
            $this->assertFalse($val);

            $val = $convert->convertToFahrenheit();
            $this->assertFalse($val);
        }


        function testWhenInputIsArray()  {
            $convert = new Convert(array());

            $val = $convert->convertToCelsius();
            $this->assertFalse($val);

            $val = $convert->convertToFahrenheit();
            $this->assertFalse($val);
        }
    }
?>

