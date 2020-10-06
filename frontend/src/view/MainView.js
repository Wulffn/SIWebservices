import React, {useEffect, useState} from 'react';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';
import DriveEtaIcon from '@material-ui/icons/DriveEta';
import Typography from '@material-ui/core/Typography';
import {makeStyles} from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import CarTable from "../components/CarTable";
import CircularProgress from "@material-ui/core/CircularProgress";
import MonetizationOnIcon from '@material-ui/icons/MonetizationOn';
import Divider from "@material-ui/core/Divider";

function Copyright() {
    return (
        <Typography variant="body2" color="textSecondary" align="center">
            {'Copyright © MWNCK '}
            {new Date().getFullYear()}
        </Typography>
    );
}

const countries = [{country: "Denmark", code: "DK"}, {country: "Deutschland", code: "DE"}]

const useStyles = makeStyles((theme) => ({
    paper: {
        marginTop: theme.spacing(8),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    avatar: {
        margin: theme.spacing(1),
        backgroundColor: theme.palette.primary.dark,
    },
    submit: {
        margin: theme.spacing(3, 0, 2),
    },
    footer: {
        position: 'fixed',
        bottom: 0
    },
    formControl: {
        margin: theme.spacing(2),
        width: '100%',
    },
    cars: {
        marginTop: theme.spacing(4)
    }
}));

export default function SignIn() {
    const classes = useStyles();
    const [car, setCar] = useState({manu: "", model: "", km: "", year: ""});
    const [currencies, setCurrencies] = useState([]);
    const [currency, setCurrency] = useState("");
    const [country, setCountry] = useState({selectCountryFrom: "", selectCountryTo: ""});
    const [duty, setDuty] = useState("")
    const [cars, setCars] = useState([]);
    const [loading, setLoading] = useState(null)
    const [loadingDuty, setLoadingDuty] = useState(null)

    const onChange = (e) => {
        const {name, value} = e.target;
        if (name === "selectCurrencies") {
            setCurrency(value);
        } else if (name === "selectCountryFrom" || name === "selectCountryTo") {
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

    const getDuty = (envelope) => {
        fetch("http://138.68.102.45:8080/soap/ws", {
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
            setLoadingDuty(false)
        })
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

    const onSubmit = (e) => {
        setLoading(true)
        const url = `http://138.68.102.45:8080/carapi/car/${car.manu}/${car.model}/${car.year}/${car.km}/${country.selectCountryFrom}/${country.selectCountryTo}/${currency}`;
        fetch(url).then(response => response.json()).then(data => {
            setCars(data)
            setLoading(false)
        });
    }

    useEffect(() => {
        fetch("https://api.exchangeratesapi.io/latest").then(response => response.json()).then(data => setCurrencies(Object.keys(data.rates)));
    }, [])

    useEffect(() => {
        if(cars.length > 0) {
            setLoadingDuty(true)
            const fromCars = cars.filter(c => country.selectCountryFrom === c.country)
            const toCars = cars.filter(c => country.selectCountryTo === c.country)
            getDuty(createEnvelope(fromCars, toCars))
        }
    }, [cars])

    return (
        <React.Fragment>
            <Grid container>
                <Grid item xs={3}>
                    <Container component="main" maxWidth="xs">
                        <CssBaseline/>
                        <div className={classes.paper}>
                            <Avatar className={classes.avatar}>
                                <DriveEtaIcon/>
                            </Avatar>
                            <Typography component="h1" variant="h5">
                                Enter car details
                            </Typography>
                            <TextField
                                margin="normal"
                                label="Manufacturer"
                                required
                                fullWidth
                                name="manu"
                                value={car.manu}
                                onChange={onChange}/>
                            <TextField
                                margin="normal"
                                label="Model"
                                required
                                fullWidth
                                name="model"
                                value={car.model}
                                onChange={onChange}/>
                            <TextField
                                margin="normal"
                                label="Km"
                                required
                                fullWidth
                                name="km"
                                value={car.km}
                                onChange={onChange}/>
                            <TextField
                                margin="normal"
                                label="Year"
                                required
                                fullWidth
                                name="year"
                                value={car.year}
                                onChange={onChange}/>
                            <FormControl className={classes.formControl}>
                                <InputLabel required>Currency</InputLabel>
                                <Select
                                    name="selectCurrencies"
                                    value={currency}
                                    onChange={onChange}
                                >
                                    {currencies.map(c => {
                                        return (
                                            <MenuItem key={c} value={c}>
                                                {c}
                                            </MenuItem>
                                        )
                                    })
                                    }
                                </Select>
                            </FormControl>
                            <FormControl className={classes.formControl}>
                                <InputLabel required>From country</InputLabel>
                                <Select
                                    name="selectCountryFrom"
                                    value={country.selectCountryFrom}
                                    onChange={onChange}
                                >
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
                            </FormControl>
                            <FormControl className={classes.formControl}>
                                <InputLabel required>To country</InputLabel>
                                <Select
                                    name="selectCountryTo"
                                    value={country.selectCountryTo}
                                    onChange={onChange}
                                >
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
                            </FormControl>
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                color="primary"
                                onClick={onSubmit}
                                className={classes.submit}
                            >
                                Search
                            </Button>
                        </div>

                    </Container>
                </Grid>
                <Grid item xs={6}>
                    <Container component="main" maxWidth="lg">
                        <div className={classes.paper}>
                            <Avatar className={classes.avatar}>
                                <DriveEtaIcon/>
                            </Avatar>
                            <Typography component="h1" variant="h5">
                                Cars found
                            </Typography>
                            <div className={classes.cars}>
                                {loading === null ? null : loading ?
                                    <CircularProgress/>
                                :
                                    <CarTable cars={cars}/>
                                }
                            </div>
                        </div>
                    </Container>
                </Grid>
                <Grid item xs={3}>
                    <Container component="main" maxWidth="xs">
                        <div className={classes.paper}>
                            <Avatar className={classes.avatar}>
                                <MonetizationOnIcon/>
                            </Avatar>
                            <Typography component="h1" variant="h5">
                                Duty
                            </Typography>
                            <div className={classes.cars}>
                                {loadingDuty === null ? null : loadingDuty ?
                                    <CircularProgress/>
                                    :
                                    <Typography variant={"h5"}>{duty} {currency}</Typography>
                                }
                            </div>
                        </div>
                    </Container>
                </Grid>
            </Grid>
            <footer className={classes.footer}>
                <Box mt={8}>
                    <Copyright/>
                </Box>
            </footer>
        </React.Fragment>
    );
}