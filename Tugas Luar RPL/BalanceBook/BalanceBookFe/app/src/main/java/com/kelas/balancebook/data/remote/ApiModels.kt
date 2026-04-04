package com.kelas.balancebook.data.remote

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val currency: String
)

data class AuthResponse(
    val message: String,
    val token: String,
    val user: UserDto
)

data class MessageResponse(
    val message: String
)

data class MeResponse(
    val user: UserDto
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class GoogleLoginRequest(
    @SerializedName("id_token")
    val idToken: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)

data class TransactionDto(
    val id: Long,
    val type: String,
    val category: String,
    val note: String?,
    val amount: Double,
    @SerializedName("transaction_date")
    val transactionDate: String
)

data class TransactionListResponse(
    val transactions: List<TransactionDto>
)

data class CreateTransactionRequest(
    val type: String,
    val category: String,
    val note: String?,
    val amount: Double,
    @SerializedName("transaction_date")
    val transactionDate: String
)

data class CreateTransactionResponse(
    val message: String,
    val transaction: TransactionDto
)

data class TopCategoryDto(
    val category: String,
    val amount: Double,
    val percent: Double
)

data class DashboardSummaryDto(
    @SerializedName("current_balance")
    val currentBalance: Double,
    @SerializedName("month_income")
    val monthIncome: Double,
    @SerializedName("month_expense")
    val monthExpense: Double,
    @SerializedName("month_savings")
    val monthSavings: Double,
    @SerializedName("chart_total")
    val chartTotal: Double,
    @SerializedName("top_categories")
    val topCategories: List<TopCategoryDto>
)

data class DashboardSummaryResponse(
    val summary: DashboardSummaryDto
)

data class TimelinePointDto(
    @SerializedName("month_key")
    val monthKey: String,
    val amount: Double
)

data class ReportDto(
    val period: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("monthly_expenses")
    val monthlyExpenses: Double,
    @SerializedName("expense_change_percent")
    val expenseChangePercent: Double,
    val income: Double,
    val expenses: Double,
    val savings: Double,
    @SerializedName("net_cash_flow")
    val netCashFlow: Double,
    @SerializedName("top_categories")
    val topCategories: List<TopCategoryDto>,
    val timeline: List<TimelinePointDto>
)

data class ReportResponse(
    val report: ReportDto
)

data class SettingsDto(
    val currency: String
)

data class SettingsResponse(
    val settings: SettingsDto
)

data class UpdateSettingsRequest(
    val name: String? = null,
    val currency: String? = null
)

data class UpdateSettingsResponse(
    val message: String,
    val user: UserDto
)
