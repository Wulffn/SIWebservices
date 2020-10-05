import React, {useEffect, useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from "@material-ui/core/Button";
import {Divider, Grid, Typography} from "@material-ui/core";

import {makeStyles} from '@material-ui/core/styles';
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import Checkbox from "@material-ui/core/Checkbox";
import FormControlLabel from "@material-ui/core/FormControlLabel";

const countries = [{country: "Denmark", code: "DK"}, {country: "Deutschland", code: "DE"}]

const useStyles = makeStyles((theme) => ({
    button: {

        margin: theme.spacing(1),
        width: 200,

    },
}));

function App() {
    const classes = useStyles();
    const [car, setCar] = useState({manu: "", model: "", km: "", year: ""});
    const [currencies, setCurrencies] = useState([]);
    const [currency, setCurrency] = useState("");
    const [country, setCountry] = useState({selectCountryFrom: "", selectCountryTo: ""});
    const [duty, setDuty] = useState("")

    const [cars, setCars] = useState([]);

    const onChange = (e) => {
        const {name, value} = e.target;
        if (name === "selectCurrencies") {
            setCurrency(value);
        } else if(name === "selectCountryFrom" || name === "selectCountryTo") {
            setCountry({
                ...country,
                [name]: value
            })
        } else {
            setCar({
                ...car, [name]: value
            })
        }
    }
    const onSubmit = (e) => {
        const url = `http://localhost:8081/car/${car.manu}/${car.model}/${car.year}/${car.km}/${country.selectCountryFrom}/${country.selectCountryTo}/${currency}`;
        fetch(url).then(response => response.json()).then(data => setCars(data));
    }

    const createEnvelope = (fromCars, toCars) => {
        const envelope =
            "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <Body>\n" +
            "        <getCarDutyRequest xmlns=\"http://www.mwnck.dk/springsoap/gen\">\n" +
            fromCars.map(c => {
                return `<taxatedCar><price>${c.price}</price></taxatedCar>`
            }).join("") +
            toCars.map(c => {
                return `<car><price>${c.price}</price></car>`
            }).join("") +
            "        </getCarDutyRequest>\n" +
            "    </Body>\n" +
            "</Envelope>"
        return envelope
    }

    const getDuty = (envelope) => {
        fetch("http://localhost:8080/ws", {
            method: 'POST',
            body: envelope,
            headers: {
                'Content-Type': 'text/xml'
            }
            }).then(res => res.text()).then(data => {
            let parser = new DOMParser()
            let doc = parser.parseFromString(data, "text/xml")
            const duty = doc.getElementsByTagName("ns2:duty")[0].childNodes[0].nodeValue
            const roundedDuty = Math.round(Number.parseFloat(duty) * 100) / 100
            setDuty(roundedDuty)
        })
    }

    useEffect(() => {
        fetch("https://api.exchangeratesapi.io/latest").then(response => response.json()).then(data => setCurrencies(Object.keys(data.rates)));
    }, [])

    useEffect(() => {
        if(cars.length > 0) {
            const fromCars = cars.filter(c => country.selectCountryFrom === c.country)
            const toCars = cars.filter(c => country.selectCountryTo === c.country)
            getDuty(createEnvelope(fromCars, toCars))
        }
    }, [cars])

    return (
        <div className="App">
            <Grid container spacing={10}>
                <Grid item xs={3}>
                    <TextField className={classes.button} fullWidth placeholder="manufacturer" name="manu"
                               value={car.manu} onChange={onChange}/>

                    <TextField className={classes.button} fullWidth placeholder="model" name="model" value={car.model}
                               onChange={onChange}/>

                    <TextField className={classes.button} fullWidth placeholder="km" name="km" value={car.km}
                               onChange={onChange}/>

                    <TextField className={classes.button} fullWidth placeholder="year" name="year" value={car.year}
                               onChange={onChange}/>

                    <Select name="selectCurrencies" value={currency} className={classes.button} fullWidth
                            placeholder="select currency" onChange={onChange}>
                        {currencies.map(currency => <MenuItem key={currency} value={currency}>{currency}</MenuItem>)}
                    </Select>
                    <Select name="selectCountryFrom" value={country.selectCountryFrom} className={classes.button} fullWidth
                            placeholder="select currency" onChange={onChange}>
                        {countries.map(c => {
                            return (
                                <MenuItem key={c.code} value={c.code}>
                                    <img height={25} width={25}
                                         src={`https://www.countryflags.io/${c.code}/flat/64.png`}/>
                                    {c.country}
                                </MenuItem>
                            )
                        })
                        }
                    </Select>
                    <Select name="selectCountryTo" value={country.selectCountryTo} className={classes.button} fullWidth
                            placeholder="select currency" onChange={onChange}>
                        {countries.map(c => {
                            return (
                                <MenuItem key={c.code + "1"} value={c.code}>
                                    <img height={25} width={25}
                                         src={`https://www.countryflags.io/${c.code}/flat/64.png`}/>
                                    {c.country}
                                </MenuItem>
                            )
                        })
                        }
                    </Select>
                    <Button className={classes.button} onClick={onSubmit} variant="outlined">Search cars</Button>
                </Grid>
                <Grid item xs={3}>
                    <Typography variant="h6">Calculated duty</Typography>
                    <Typography variant="subtitle1">{duty} {currency}</Typography>
                </Grid>
            </Grid>


        </div>
    );
}

export default App;
