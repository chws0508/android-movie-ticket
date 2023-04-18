package woowacourse.movie.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import woowacourse.movie.R
import woowacourse.movie.domain.Movie
import woowacourse.movie.domain.Ticket
import woowacourse.movie.domain.discountPolicy.DisCountPolicies
import woowacourse.movie.domain.discountPolicy.MovieDay
import woowacourse.movie.domain.discountPolicy.OffTime
import woowacourse.movie.dto.MovieDto
import woowacourse.movie.dto.MovieDtoConverter
import woowacourse.movie.getSerializableCompat
import woowacourse.movie.view.Counter
import woowacourse.movie.view.DateSpinner
import woowacourse.movie.view.MovieDateTimePicker
import woowacourse.movie.view.MovieView
import woowacourse.movie.view.TimeSpinner
import java.time.LocalDateTime

class MovieReservationActivity : AppCompatActivity() {
    private val counter: Counter by lazy {
        Counter(
            findViewById(R.id.movie_reservation_people_count_minus),
            findViewById(R.id.movie_reservation_people_count_plus),
            findViewById(R.id.movie_reservation_people_count),
            COUNTER_SAVE_STATE_KEY
        )
    }
    private val movieDateTimePicker: MovieDateTimePicker by lazy {
        MovieDateTimePicker(
            DateSpinner(
                findViewById(R.id.movie_reservation_date_spinner),
                DATE_SPINNER_SAVE_STATE_KEY,
            ),
            TimeSpinner(
                findViewById(R.id.movie_reservation_time_spinner),
                TIME_SPINNER_SAVE_STATE_KEY,
            )
        )
    }
    private val reservationButton: Button by lazy {
        findViewById(R.id.movie_reservation_button)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_reservation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val movieDto = getMovieDto()
        val movie = getMovie(movieDto)
        if (movieDto != null) renderMovieView(movieDto)
        if (movie != null) {
            counter.load(savedInstanceState)
            movieDateTimePicker.makeView(movie, savedInstanceState)
            reservationButtonClick(movie)
        }
    }

    private fun renderMovieView(movieDto: MovieDto) {
        MovieView(
            findViewById(R.id.movie_reservation_poster),
            findViewById(R.id.movie_reservation_title),
            findViewById(R.id.movie_reservation_date),
            findViewById(R.id.movie_reservation_running_time),
            findViewById(R.id.movie_reservation_description)
        ).render(movieDto)
    }

    private fun getMovieDto(): MovieDto? {
        return intent.extras?.getSerializableCompat(MOVIE_KEY_VALUE)
    }

    private fun getMovie(movieDto: MovieDto?): Movie? {
        return movieDto?.let { MovieDtoConverter().convertDtoToModel(it) }
    }

    private fun reservationButtonClick(movie: Movie) {
        reservationButton.setOnClickListener {
            val date = LocalDateTime.of(
                movieDateTimePicker.dateSpinner.getSelectedDate(),
                movieDateTimePicker.timeSpinner.getSelectedTime()
            )
            val discountPolicies = DisCountPolicies(listOf(MovieDay(), OffTime()))
            val peopleCount = counter.getCount()
            val ticket = Ticket(date, peopleCount, discountPolicies)
            val reservation = movie.makeReservation(ticket)
            ReservationResultActivity.start(this, reservation)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        counter.save(outState)
        movieDateTimePicker.save(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun start(context: Context, movie: Movie) {
            val intent = Intent(context, MovieReservationActivity::class.java)
            val movieDto = MovieDtoConverter().convertModelToDto(movie)
            intent.putExtra(MOVIE_KEY_VALUE, movieDto)
            context.startActivity(intent)
        }

        private const val MOVIE_KEY_VALUE = "movie"
        private const val COUNTER_SAVE_STATE_KEY = "counter"
        private const val DATE_SPINNER_SAVE_STATE_KEY = "date_spinner"
        private const val TIME_SPINNER_SAVE_STATE_KEY = "time_spinner"
    }
}
