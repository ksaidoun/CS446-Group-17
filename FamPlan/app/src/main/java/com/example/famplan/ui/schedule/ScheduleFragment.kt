package com.example.famplan.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.famplan.databinding.FragmentScheduleBinding
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val scheduleViewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)

        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Display current month
        displayCurrentMonth()

        // Generate and display the weekly calendar
        displayWeeklyCalendar()

        /*
        val textView: TextView = binding.textSchedule
        displayWeeklyCalendar()
        scheduleViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
         */

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayWeeklyCalendar() {
        val calendar = Calendar.getInstance()

        val weekDays = ArrayList<String>()

        // Get the first day of the week (Sunday)
        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        // Add the dates for the current week
        repeat(7) {
            // Get the abbreviated day name (EEE) and the day of the month (dd)
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
            val displayText = "$dayOfWeek\n$dayOfMonth"
            weekDays.add(displayText)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Display the dates in the TextViews
        binding.apply {
            // Assuming you have TextViews for each day of the week
            textSunday.text = weekDays[0]
            textMonday.text = weekDays[1]
            textTuesday.text = weekDays[2]
            textWednesday.text = weekDays[3]
            textThursday.text = weekDays[4]
            textFriday.text = weekDays[5]
            textSaturday.text = weekDays[6]
        }
    }

    private fun displayCurrentMonth() {
        val calendar = currentWeekStartDate.clone() as Calendar
        val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
        binding.textSchedule.text = monthName
    }


    private var currentWeekStartDate: Calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize current week start date to the beginning of the current week
        currentWeekStartDate.firstDayOfWeek = Calendar.SUNDAY
        currentWeekStartDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        // Display the current week
        displayCurrentWeek()

        binding.btnPreviousWeek.setOnClickListener {
            navigateToPreviousWeek()
        }

        binding.btnNextWeek.setOnClickListener {
            navigateToNextWeek()
        }
    }

    private fun displayCurrentWeek() {
        val calendarStart = currentWeekStartDate.clone() as Calendar
        val calendarEnd = currentWeekStartDate.clone() as Calendar
        calendarEnd.add(Calendar.DAY_OF_MONTH, 6) // Set calendarEnd to the end of the week

        binding.apply {
            // Display the dates for each day of the week
            val sdfDay = SimpleDateFormat("EEE", Locale.getDefault())
            val sdfDate = SimpleDateFormat("dd", Locale.getDefault())

            textSunday.text = "${sdfDay.format(calendarStart.time)}\n${sdfDate.format(calendarStart.time)}"
            calendarStart.add(Calendar.DAY_OF_MONTH, 1)
            textMonday.text = "${sdfDay.format(calendarStart.time)}\n${sdfDate.format(calendarStart.time)}"
            calendarStart.add(Calendar.DAY_OF_MONTH, 1)
            textTuesday.text = "${sdfDay.format(calendarStart.time)}\n${sdfDate.format(calendarStart.time)}"
            calendarStart.add(Calendar.DAY_OF_MONTH, 1)
            textWednesday.text = "${sdfDay.format(calendarStart.time)}\n${sdfDate.format(calendarStart.time)}"
            calendarStart.add(Calendar.DAY_OF_MONTH, 1)
            textThursday.text = "${sdfDay.format(calendarStart.time)}\n${sdfDate.format(calendarStart.time)}"
            calendarStart.add(Calendar.DAY_OF_MONTH, 1)
            textFriday.text = "${sdfDay.format(calendarStart.time)}\n${sdfDate.format(calendarStart.time)}"
            calendarStart.add(Calendar.DAY_OF_MONTH, 1)
            textSaturday.text = "${sdfDay.format(calendarStart.time)}\n${sdfDate.format(calendarStart.time)}"
        }
    }

    private fun navigateToPreviousWeek() {
        currentWeekStartDate.add(Calendar.DAY_OF_MONTH, -7)
        displayCurrentWeek()
        displayCurrentMonth()
    }

    private fun navigateToNextWeek() {
        currentWeekStartDate.add(Calendar.DAY_OF_MONTH, 7)
        displayCurrentWeek()
        displayCurrentMonth()
    }


}