package com.phemex.dataFactory.common.exception;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.exception.CommonMessageCode
 * @Date: 2022年10月28日 11:54
 * @Description:
 */
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonMessageCode implements PhemexMessageCode {

    INVALID_ARGS(30000, "phemex.error.common.invalidargs"),

    INTERNAL_ERROR(30001, "phemex.error.common.internalerror"),

    MAIL_SEND_ERROR(30002, "phemex.mail.sendError"),

    MAIL_OTP_VERIFY_CODE_MISSED(30003, "phemex.error.verify.code.missed"),

    OTP_VERIFY_CODE_ERROR(30004, "phemex.error.otp.code"),

    MAIL_VERIFY_CODE_ERROR(30005, "phemex.error.mail.code"),

    REQUEST_TOKEN_ERROR(30006, "phemex.error.request.token"),

    REQUEST_EXPIRED(30007, "phemex.request.expired"),

    SYMBOL_NOT_SUPPORTED(30008, "phemex.symbol.not_supported"),

    BIND_GOOGLE2FA_FIRST(30009, "phemex.totp.should_bind_first"),

    HUMAN_BEHAVIOR_CHECK_ERROR(30010, "phemex.human.behavior.check_erorr"),

    INVALID_TYPE(30011, "phemex.error.common.invalidtype"),

    QUOTE_PRICE_EXPIRED(30012, "phemex.quote.expired"),

    QUOTE_PRICE_ERROR(30013, "phemex.quote.price.failed"),

    BTC_QUOTE_PRICE_LOW_LIMIT(30014, "phemex.btc.quote.lowlimit"),

    USD_QUOTE_PRICE_RANGE_LIMIT(30015, "phemex.usd.quote.lowlimit"),

    TRANSFER_BALANCE_LOW_LIMIT(30016, "phemex.transfer.lowlimit"),

    QUOTE_SERVICE_NOT_AVAILABLE(30017, "phemex.quote.notready"),

    DATA_SIZE_UPLIMIT(30018, "phemex.data.size.uplimt"),

    TOO_MANY_REQUESTS(30019, "phemex.request.throttle"),

    APP_NO_NEW_RELEASE(30020, "phemex.release.info"),

    STR_ARG_LEN_ERR(30021, "phemex.str.args.len.err"),

    APIKEY_ADDR_ERR(30022, "phemex.apikey.addr.err"),

    MAIL_IN_BLACKLIST(30033, "phemex.mail.not.support"),

    SERVICE_MARGIN_TRANSFER_MAINTAIN(30034, "phemex.margin.transfer.maintain"),

    TICK_SERVICE_NOT_AVAILABLE(30035, "phemex.tick.not.ready"),

    COIN_NOT_SUPPORTED(30036, "phemex.coin.not.support"),

    ORDER_TYPE_NOT_SUPPORTED(30037, "phemex.order.type.not.support"),

    ORDER_STATUS_NOT_SUPPORTED(30038, "phemex.order.status.not.support"),

    ADDRESS_TAG_IS_TOO_LONG(30039, "tag is too long"),

    CURRENCY_IS_NOT_SUPPORT(30040, "phemex.currency.is.not.support"),

    REQUEST_TIMEOUT(30044, "phemex.request.timedout"),

    SPECIAL_CHAR_PROHIBITED(30045, "phemex.special.charactor.prohibited"),

    ERROR_WITHDRAW_ADDRESS(30046, "phemex.withdraw.address.error"),// 改错误码不应该展示给正常页面操作的用户

    WITHDRAW_ADDRESS_NOT_MATCH_SHAKEY(30047, "phemex.withdraw.address.notmatch.shakey"),
    DEPOSIT_ADDRESS_NOT_MATCH_SHAKEY(30048, "phemex.deposit.address.notmatch.shakey"),

    WITHDRAW_ADDRESS_NOT_MATCH(30049, "phemex.withdraw.address.not.match"),

    DEPOSIT_ADDRESS_NOT_FOUND(30051, "phemex.payment.request.address.not.fount"),

    PAYMENT_PAY_WAY_NOT_FOUND(30052, "phemex.payment.pay.way.not.fount"),

    PAYMENT_PAYMENT_CREATE_REQUEST_ERROR(30053, "phemex.payment.create.request.error"),

    PAYMENT_PAYMENT_PRICING_NOT_FOUND(30054, "phemex.payment.pricing.not.fount"),

    PAYMENT_PAYMENT_CHANNEL_NOT_FOUND(30055, "phemex.payment.channel.not.fount"),

    PHEMEX_CONFIG_SUPPORT_LANG_NOT_FOUND(30056, "phemex.lang.not.support"),

    WITHDRAW_AMOUNT_SHOULD_BE_INT(30061, "withdrawal.amount.invalid"),

    FEE_RATE_DISCOUNT_TYPE_NOT_SUPPORT(30058, "phemex.fee.rate.discount.type.not.support"),

    FEE_RATE_RIGHT_DISCOUNT_RATE_INVALID(30059, "phemex.fee.rate.discount.right.rate.invalid"),

    FEE_RATE_UNKNOWN_OPERA_TYPE(30060, "phemex.fee.rate.opera.type.not.supported"),

    SERVICE_IN_MAINTAIN(30062, "Service In maintenance"),

    MAIL_TEMPLATES_EQUAL(30063, "mail templates equal."),
    MAIL_TEMPLATES_OF_TARGET_ENV_FRESHER(30064, "mail templates of target env is fresher than that of current env."),

    NICKNAME_INVALID(30065, "phemex.user.nickname.invalid"),

    NICKNAME_TOO_LONG(30066, "phemex.user.nickname.length"),


    DELETE_WHITE_ADDRESS_ERROR(30070, "phemex.delete.withdraw.white.address.fail"),

    DELETE_CONVRTSION_WHITE_ADDRESS_ERROR(30071, "phemex.delete.conversion.white.address.fail"),

    NOT_FOUND_CLIENT(30090, "phemex.client.not.found"),

    PLATFORM_REBATE_ORDER_IS_NOT_TAKER_FILL(30100, "platform.rebate.order.is.not.taker.fill"),
    PLATFORM_REBATE_PLATFORM_NOT_FOUND(30101, "platform.rebate.platform.not.found"),

    AUTO_AUDIT_WITHDRAW_FAIL(30110, "auto.audit.withdraw.fail"),

    SQS_EVENT_TYPE_ENUM_LOST(31000, "phemex.sqs.event.type.enum.lost"),

    NOT_SUPPORTED_ERC20_CONTRACT_NAME(31100, "not.supported.erc20.contract.name"),
    NOT_SUPPORTED_CURRENCY_FOR_DEPOSIT(31101, "not.supported.currency.for.deposit"),

    WITHDRAW_CLOSED_FOR_BCH(31102, "withdraw.closed.for.bch"),
    WITHDRAW_CLOSED(31103, "withdraw.closed"),

    VIP_MM_MEMBER_HAD_APPROVED(31105, "vip.mm.member.had.approved"),
    VIP_MM_MEMBER_IS_PENDING(31106, "vip.mm.member.is.pending"),

    REGISTER_INFO_INVALID(31107, "register.info.is.invalid"),

    REGISTER_INFO_NOT_CONFIRM(31108, "register.is.not.completed"),

    CURRENT_CURRENCY_CHAIN_NOT_SUPPORT_RATIO_FEE(31109, "Sorry，for this crypto withdraw，please upgrade to the latest version of the application and try to withdraw again"),

    WITHDRAW_INPUT_FEE_ERROR(31110, "withdraw.input.fee.error"),

    TIP_CLIENT_PWD_CHANGED(39991, "phemex.client.pwd.changed"),

    TIP_CLIENT_TOTP_CHANGED(39992, "phemex.client.2fa.changed"),

    DATA_STATUS_ERROE(39993, "phemex.data.status.error"),

    FILE_SIZE_TOO_LARGE(39994, "phemex.file.size.error"),

    JPUSH_ERROR(39995, "phemex.jpush.error"),

    DATA_LIMIT(39996, "phemex.data.limit"),

    API_REPEAT_CALL(39997, "phemex.api.repeat.call"),

    PROCESS_CLOSED_PNL(40001, "phemex.job.process.closed.pnl"),

    PROCESS_FUNDING(40002, "phemex.job.process.funding"),

    PROCESS_DAILY_PNL(40003, "phemex.job.process.daily.pnl"),

    TIP_CLIENT_TOTP_RESETED(40200, "phemex.client.2fa.reseted"),
    UPDATE_USER_TAG_FAILED(40005, "phemex.update.user.tag.failed"),

    DELETE_ACCOUNT_LOGOUT(40006, "phemex.delete.account.logout"),





    NOT_MATCH_NEW_WALLET_CLIENT(40100, "phemex.not.match.new.wallet.client"),
    NOT_FOUND_DISTRIBUTE_DEPOSIT_ADDRESS(40101, "phemex.not.found.distribute.deposit.address"),
    RETURN_UNKNOWN_HTTP_CODE_WHILE_SEND_TO_TE(40102, "phemex.return.unknown.http.code.while.send.to.TE"),
    WITHDRAW_AMOUNT_NOT_MATCH_PRECISION(40103, "phemex.withdraw.amount.not.match.precision"),
    CHAIN_INFO_NOT_UNIQUE(40104, "chain.info.not.unique"),

    EXCEED_RISK_EXPOSURE_LOWER_LIMIT(40107, "phemex.exceed.risk.exposure.lower.limit"),
    EXCEED_RISK_EXPOSURE_UPPER_LIMIT(40108, "phemex.exceed.risk.exposure.upper.limit"),
    NOT_FINISHED_KYC(40109, "phemex.not.finished.kyc"),
    CONVERSION_PRICE_EXCEED_FLUCTUATION_LIMIT(40110, "phemex.conversion.exceed.price.fluctuation"),
    TODAY_BONUS_ALREADY_DISTRIBUTED(40111, "phemex.today.bonus.already.distributed. please update cnt"),
    SOCIAL_MEDIA_BONUS_ALREADY_DISTRIBUTED(40112, "phemex.social.media.bonus.already.distributed"),

    FILE_TYPE_NOT_SUPPORTED(40113, "phemex.file.type.not.supported"),
    IMAGE_TYPE_NOT_SUPPORTED(40114, "phemex.image.type.not.supported"),
    COUNTRY_NOT_FOUND(40115, "phemex.country.not.found"),
    FILE_UPLOAD_ONFIDO_FAILED(40116, "phemex.upload.onfido.failed"),

    SUB_USER_HAS_NO_PERMISSIO(40117, "phemex.sub.user.has.no.permision.to.create.antiPhishingCode"),
    WITHDRAW_NOT_ALLOWED(40118, "phemex.withdraw.not.allowed"),
    DEPOSIT_NOT_ALLOWED(40119, "phemex.deposit.not.allowed"),

    APP_NACOS_CONFIG_PARAMETER_UNKNOWN(40120, "app.nacos.config.parameter.unknown"),
    DATA_REPORT_ERROR(40121, "data.report.error"),

    WITHDRAW_ADDRESS_AT_RISK(40122, "withdraw.address.at.risk"),

    PRICING_CURRENCY_CONVERT_ERROR(40123, "pricing.currency.convert.error"),

    B2C_TRADE_IS_FORBIDDEN(40124, "b2c.trade.is.forbidden"),

    // add error code for contract [90001-99999]
    CONTRACT_FEE_UPDATE_LOCK_TIMEOUT(91001, "phemex.contract.feeUpdate.lock.timeout"),

    ;


    private int code;

    private String key;

}
