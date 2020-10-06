import React, {useEffect} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

const useStyles = makeStyles({
    table: {
        minWidth: 650,
    },
});


export default function CarTable(props) {
    const classes = useStyles();

    useEffect(() => {
        console.log(props.cars)
    }, [props])

    return (
        <TableContainer component={Paper}>
            <Table className={classes.table} aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <TableCell>Car</TableCell>
                        <TableCell align="right">Mileage</TableCell>
                        <TableCell align="right">Year</TableCell>
                        <TableCell align="right">Price</TableCell>
                        <TableCell align="right">Country</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {props.cars.map((car, idx) => (
                        <TableRow key={car.manufacturer + idx}>
                            <TableCell component="th" scope="row">
                                {car.manufacturer} {car.model}
                            </TableCell>
                            <TableCell align="right">{car.km}</TableCell>
                            <TableCell align="right">{car.year}</TableCell>
                            <TableCell align="right">{Math.round(Number.parseFloat(car.price) * 100) / 100} {car.currency}</TableCell>
                            <TableCell align="right">{car.country}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
}
