import { HttpInterceptorFn } from '@angular/common/http';
import { DateTime } from 'luxon';

export const dateInterceptor: HttpInterceptorFn = (req, next) => {
  const formatDates = (body: any): any => {
    if (body === null || typeof body !== 'object') return body;

    for (const key of Object.keys(body)) {
      const value = body[key];
      // Si c'est un objet Luxon, on le transforme en YYYY-MM-DD
      if (DateTime.isDateTime(value)) {
        body[key] = value.toISODate();
      } else if (value instanceof Date) {
        body[key] = DateTime.fromJSDate(value).toISODate();
      } else if (typeof value === 'object') {
        formatDates(value);
      }
    }
    return body;
  };

  if (req.body) {
    req = req.clone({ body: formatDates({ ...req.body }) });
  }
  return next(req);
};
