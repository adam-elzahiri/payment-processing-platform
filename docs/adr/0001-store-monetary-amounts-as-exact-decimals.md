# ADR-0001: Store monetary amounts as exact decimals
- **Status:** Accepted
- **Date:** 2026-10-29

## Context

Transfers and account balances require exact decimal arithmetic. The platform is designed to support multiple currencies, whose valid fractional scales differ. A shared database column must accommodate the supported values, while application rules must reject amounts with invalid precision for their currency.

## Decision

Use PostgreSQL `NUMERIC(20,4)` for account balances and ledger amounts, and Java `BigDecimal` for monetary values. Store each amount with its currency code.

`NUMERIC(20,4)` provides up to 16 whole-number digits and four fractional digits. The fourth fractional place is storage capacity; it does not mean every supported currency may use four decimal places. Currency metadata and application validation define each currency's permitted scale before persistence. This validation is required because PostgreSQL may round values to a column's declared scale.

Store values in their own currency's major units. For example, `12.34 EUR` is stored as `12.34` with currency `EUR`, rather than converted to `1234`. An account is denominated in one currency, and the first transfer feature permits transfers only between accounts in the same currency. Cross-currency conversion is outside its scope.

Do not use `float` or `double` for monetary amounts. Parse external decimal input without first converting it through binary floating-point.

## Alternatives considered

### `BIGINT` in minor units

Store an integer count of the currency's minor units, such as `1234` with `EUR` for €12.34.

**Pros**
- Exact integer arithmetic for posted amounts.
- Fixed-size representation and efficient integer operations.
- Commonly used by payment APIs.

**Cons**
- Requires currency-specific conversion when parsing and displaying amounts.
- Fee and rate calculations can produce fractions of a minor unit that require explicit rounding.
- The integer cannot be interpreted without the currency and its scale.

### `NUMERIC(19,3)`

Store major currency units with three fractional digits.

**Pros**
- Exact decimal representation with up to 16 whole-number digits.
- Simpler to inspect than integer minor units.

**Cons**
- Provides no fourth fractional place if future supported calculations or units require it.

### `NUMERIC(19,4)`

Store major currency units with four fractional digits.

**Pros**
- Exact decimal representation with four fractional digits.
- Simpler to inspect than integer minor units.

**Cons**
- Provides only 15 whole-number digits, one fewer than the selected `NUMERIC(20,4)`.
- Like any fixed-scale choice, still requires currency-specific validation and rounding rules.

## Consequences

- Java code uses `BigDecimal`; input should come from an exact decimal representation such as a string, not from `double`.
- All account balances and ledger postings use the same database type. The currency code determines the unit, and currency metadata determines the permitted fractional scale.
- PostgreSQL's `NUMERIC(20,4)` stores values at four decimal places. Presentation formats the amount according to its currency; for example, a zero-decimal currency is displayed without fractional digits.
- Domain validation must reject excess fractional digits before persistence to prevent silent database rounding.
- Any fee, exchange-rate calculation, or other result smaller than a currency's permitted unit requires an explicit rounding rule before posting.
- The choice preserves a 16-digit whole-number range while adding one fractional place over `NUMERIC(19,3)`. It does not remove the need to revisit the schema if future requirements exceed these limits.
