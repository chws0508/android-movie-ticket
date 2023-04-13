package woowacourse.movie

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.movie.domain.Movie

class MovieReservationActivity : AppCompatActivity() {
    private val counter: SaveStateCounter by lazy {
        SaveStateCounter(
            Counter(
                findViewById(R.id.movie_reservation_people_count_minus),
                findViewById(R.id.movie_reservation_people_count_plus),
                findViewById(R.id.movie_reservation_people_count),
                INITIAL_COUNT,
            ),
            COUNTER_SAVE_STATE_KEY,
        )
    }

    private val dateSpinner: DateSpinner by lazy {
        DateSpinner(
            SaveStateSpinner(
                DATE_SPINNER_SAVE_STATE_KEY,
                findViewById(R.id.movie_reservation_date_spinner),
            )
        )
    }

    private val timeSpinner: TimeSpinner by lazy {
        TimeSpinner(
            SaveStateSpinner(
                TIME_SPINNER_SAVE_STATE_KEY,
                findViewById(R.id.movie_reservation_time_spinner),
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_reservation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val movie = intent.extras?.getSerializable<Movie>(getString(R.string.movie_extra_name))

        counter.applyToView()

        if (movie != null) {
            counter.load(savedInstanceState)

            dateSpinner.make(this, savedInstanceState, movie, timeSpinner)

            MovieController(
                this,
                movie,
                findViewById(R.id.movie_reservation_poster),
                findViewById(R.id.movie_reservation_title),
                findViewById(R.id.movie_reservation_date),
                findViewById(R.id.movie_reservation_running_time),
                findViewById(R.id.movie_reservation_description)
            ).render()

            ReservationButton(
                findViewById(R.id.movie_reservation_button),
                getString(R.string.reservation_extra_name),
                this,
                movie,
                dateSpinner,
                timeSpinner,
                counter.counter
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        counter.save(outState)
        dateSpinner.save(outState)
        timeSpinner.save(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val INITIAL_COUNT = 1
        const val COUNTER_SAVE_STATE_KEY = "counter"
        const val DATE_SPINNER_SAVE_STATE_KEY = "date_spinner"
        const val TIME_SPINNER_SAVE_STATE_KEY = "time_spinner"
    }
}
