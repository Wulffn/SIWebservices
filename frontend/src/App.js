import React, {useEffect, useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from "@material-ui/core/Button";
import {Divider, Grid, Typography} from "@material-ui/core";

import { makeStyles } from '@material-ui/core/styles';
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import Checkbox from "@material-ui/core/Checkbox";
import FormControlLabel from "@material-ui/core/FormControlLabel";

const useStyles = makeStyles((theme) => ({
    button: {

            margin: theme.spacing(1),
            width: 200,

    },
}));

function App() {
  const classes=useStyles();
  const [car, setCar] = useState({manu:"", model:"", km:"", year:""});
  const [scraper, setScraper] = useState("mobilede");
  const [scrapers, setScrapers] = useState([]);
  const [currencies, setCurrencies] = useState([]);
  const [currency, setCurrency] = useState("");
  const [cars, setCars] = useState([]);

  const onChange = (e) => {
    const {name, value} = e.target;
    if (name === "selectCurrencies")
    {
        setCurrency(value);
    }
    else {
        setCar({
            ...car, [name]: value
        })
    }
  }
  const onSubmit = (e) => {
      console.log("Hit me");
    const url = `http://localhost:8080/car/${car.manu}/${car.model}/${car.year}/${car.km}/${scraper}/${currency}`;
    fetch(url).then(response => response.json()).then(data => setCars(data));
  }

  useEffect(()=>{
      fetch("https://api.exchangeratesapi.io/latest").then(response => response.json()).then(data => setCurrencies(Object.keys(data.rates)));
      fetch("http://localhost:8080/car/").then(response => response.json()).then(data => setScrapers(data));
  },[])

  return (
    <div className="App">
        <Grid container spacing={10}>
            <Grid item xs={3}>
      <TextField className={classes.button} fullWidth placeholder="manufacturer" name="manu" value={car.manu} onChange={onChange} />

      <TextField className={classes.button} fullWidth placeholder="model" name="model" value={car.model} onChange={onChange} />

      <TextField className={classes.button} fullWidth placeholder="km" name="km" value={car.km} onChange={onChange} />

      <TextField className={classes.button} fullWidth placeholder="year" name="year" value={car.year} onChange={onChange} />

      <Select name="selectCurrencies" value={currency} className={classes.button} fullWidth placeholder="select currency" onChange={onChange}>
          {currencies.map(currency => <MenuItem value={currency}>{currency}</MenuItem>)}
      </Select>
                <Typography variant={"h5"}>
                    Taxated cars.
                </Typography>
                {scrapers.map(scraper => <FormControlLabel control={<Checkbox name={scraper} />} label={scraper} />)}
                <Divider />
                <Typography variant={"h5"}>
                    Regular cars.
                </Typography>
                {scrapers.map(scraper => <FormControlLabel control={<Checkbox name={scraper} />} label={scraper} />)}




      <Button className={classes.button} onClick={onSubmit} variant="outlined">Search cars</Button>
            </Grid>
        </Grid>


    </div>
  );
}

export default App;
