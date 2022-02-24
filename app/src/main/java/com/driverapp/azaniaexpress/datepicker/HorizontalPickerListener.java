package com.driverapp.azaniaexpress.datepicker;

/**
 * Created by jhonn on 02/03/2017.
 */
public interface HorizontalPickerListener {
    void onStopDraggingPicker();
    void onDraggingPicker();
    void onDateSelected(Day item);
}