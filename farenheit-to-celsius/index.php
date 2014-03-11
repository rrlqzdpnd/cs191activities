<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Temperature Conversion</title>
        <style type="text/css">
            table   {
                text-align: center;
                border-collapse: collapse;
                width: 30%;
            }

            td, th  {
                border: solid 1px black;
                padding: 5px;
            }
        </style>
    </head>
    <body>
    <h2>Temperature Conversion</h2>
        <div>
            <form method="post" action="">
                <table>
                    <tbody>
                        <tr>
                            <td width="30%">Temperature:</td>
                            <td width="70%"><input type="text" name="temp" size="34" <?=(isset($_POST['temp'])) ? 'value='.$_POST['temp'] : '' ?> /></td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <input type="radio" id="ctof" name="type" value="1" <?=(isset($_POST['type']) && $_POST['type'] == 1) ? 'checked' : '' ?> />
                                <label for="ctof">Celsius to Fahrenheit</label>
                                <br />
                                <input type="radio" id="ftoc" name="type" value="2" <?=(isset($_POST['type']) && $_POST['type'] == 2) ? 'checked' : '' ?> />
                                <label for="ftoc">Fahrenheit to Celsius</label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2"><input type="submit" value="Convert!" /></td>
                        </tr>
                    </tbody>
                </table>
            </form>
        </div>
        <?php if(!empty($_POST)): ?>
            <?php
                require_once('convert.php');

                $temp = new Convert($_POST['temp']);
                $newtemp = ($_POST['type'] == 1) ? $temp->convertToFahrenheit() : $temp->convertToCelsius() ;
                $text = $_POST['temp'].'&#176;'.(($_POST['type'] == 1) ? 'C' : 'F' ).' &#8660; '.$newtemp.'&#176;'.(($_POST['type'] == 2) ? 'C' : 'F' );
            ?>
            <div>
                <h1><?=$text?></h1>
            </div>
        <?php endif; ?>
    </body>
</html>